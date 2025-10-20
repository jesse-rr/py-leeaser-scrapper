import time
import random
import logging
import tempfile
from concurrent.futures import ThreadPoolExecutor
from DrissionPage import ChromiumOptions, ChromiumPage
from parser import parse_page, save_results

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def create_driver():
    user_data = tempfile.mkdtemp(prefix=f"drission_{random.randint(1000,9999)}_")
    co = ChromiumOptions()
    co.set_browser_path('/usr/bin/chromium')
    co.set_user_data_path(user_data)
    co.set_local_port(random.randint(9222, 9333))
    co.arguments.extend([
        '--no-sandbox', '--disable-dev-shm-usage', '--disable-gpu',
        '--disable-software-rasterizer', '--disable-extensions',
        '--disable-background-networking', '--disable-sync',
        '--metrics-recording-only', '--disable-default-apps',
        '--disable-translate', '--safebrowsing-disable-auto-update',
        '--disable-background-timer-throttling', '--disable-breakpad',
        '--blink-settings=imagesEnabled=false', '--disable-remote-fonts',
        '--disable-features=VizDisplayCompositor,TranslateUI',
        '--disable-plugins', '--disable-logging', '--disable-hang-monitor'
    ])
    return ChromiumPage(addr_or_opts=co)

def scrape_single_url(driver, url: str) -> dict:
    try:
        logger.info(f"🌐 Loading: {url}")
        driver.get(url)
        time.sleep(2)  # wait for page content
        html = driver.html
        data = parse_page(url, html)
        if not data.get("description"):
            logger.warning(f"❌ No content found: {url}")
            data["error"] = "No content found"
        return data
    except Exception as e:
        logger.error(f"💥 Error: {url} - {e}")
        return {"url": url, "error": str(e)}

def worker(urls: list) -> list:
    driver = create_driver()
    results = []
    for url in urls:
        results.append(scrape_single_url(driver, url))
    driver.quit()
    return results

def run_crawler(urls: list, max_workers: int = 5, batch_delay=(2, 5)) -> list:
    results = []
    total_batches = (len(urls) + max_workers - 1) // max_workers
    logger.info(f"🚀 Starting crawler with Cloudflare bypass")
    logger.info(f"📊 Total URLs: {len(urls)}, Workers: {max_workers}")

    start_time_total = time.time()

    for batch_num in range(0, len(urls), max_workers):
        batch = urls[batch_num:batch_num + max_workers]
        batch_number = (batch_num // max_workers) + 1
        logger.info(f"📦 Batch {batch_number}/{total_batches} ({len(batch)} URLs)")

        start_time_batch = time.time()
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            futures = [executor.submit(worker, [url]) for url in batch]
            batch_results = [item for future in futures for item in future.result()]

        results.extend(batch_results)
        save_results(results)  # save after each batch

        success_count = len([r for r in results if "error" not in r])
        failed_count = len([r for r in results if "error" in r])
        elapsed_batch = time.time() - start_time_batch
        elapsed_total = time.time() - start_time_total

        logger.info(
            f"📊 Progress: {len(results)}/{len(urls)} - "
            f"{success_count} ✅ {failed_count} ❌ | "
            f"Batch time: {elapsed_batch:.1f}s | Total elapsed: {elapsed_total:.1f}s"
        )

        if batch_num + max_workers < len(urls):
            delay = random.uniform(*batch_delay)
            logger.info(f"⏳ Waiting {delay:.1f}s before next batch")
            time.sleep(delay)

    logger.info(
        f"🎯 Completed: {success_count}/{len(urls)} successful | " # type: ignore
        f"Total time: {time.time() - start_time_total:.1f}s"
    )
    return results
