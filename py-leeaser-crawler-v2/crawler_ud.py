import os
import time
import logging
from typing import Dict
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import undetected_chromedriver as uc
from parser import parse_page

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("crawler_ud")

def create_driver(headless: bool = False):
    options = uc.ChromeOptions()
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    options.add_argument("--disable-extensions")
    options.add_argument("--disable-background-networking")
    options.add_argument("--disable-sync")
    options.add_argument("--disable-translate")
    options.add_argument("--safebrowsing-disable-auto-update")
    options.add_argument("--blink-settings=imagesEnabled=false")
    options.add_argument("--disable-remote-fonts")
    options.add_argument("--disable-features=VizDisplayCompositor,TranslateUI")
    options.add_argument("--window-size=1920,1080")
    options.add_argument("--lang=en-US")
    if headless:
        options.add_argument("--headless=new")
        options.add_argument("--hide-scrollbars")
        options.add_argument("--mute-audio")
    driver = uc.Chrome(options=options, use_subprocess=True)
    return driver

def scrape_single_url(url: str, headless_env: bool = None) -> Dict: # type: ignore
    headless_flag = False
    if headless_env is None:
        headless_env = os.environ.get("HEADLESS", "0")
    headless_flag = str(headless_env) not in ("0", "false", "False", "")
    driver = create_driver(headless=headless_flag)
    try:
        logger.info(f"🌐 Loading: {url}")
        driver.get(url)
        wait = WebDriverWait(driver, 20, poll_frequency=0.5)
        try:
            wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, ".desc-text")))
            logger.info("✅ Found .desc-text on page")
        except Exception:
            logger.info("⚠️ .desc-text not found within timeout — capturing HTML")
        time.sleep(1)
        html = driver.page_source
        parsed = parse_page(url, html)
        parsed["html"] = html[:20000]
        if not parsed.get("description"):
            parsed["error"] = parsed.get("error", "No content found or blocked by anti-bot")
            logger.warning(f"❌ No content found: {url}")
        return parsed
    except Exception as e:
        logger.error(f"💥 Error scraping {url} - {e}")
        return {"url": url, "error": str(e)}
    finally:
        try:
            driver.quit()
        except Exception:
            pass
