import os
from pathlib import Path
from parser import save_results
from crawler_ud import scrape_single_url

def main():
    links_file = Path("data/links.txt")
    if not links_file.exists():
        print("ERROR: data/links.txt : FILE DOES NOT EXIST")
        return
    urls = [line.strip() for line in links_file.read_text().splitlines() if line.strip()]
    print(f"🕷️ Loaded {len(urls)} URLs to crawl.")
    if len(urls) == 0:
        print("No URLs found.")
        return
    test_url = urls[0]
    headless_env = os.environ.get("HEADLESS", "0")
    print(f"Running test for: {test_url}  (HEADLESS={headless_env})")
    result = scrape_single_url(test_url, headless_env) # type: ignore
    save_results([result])
    print("✅ Crawling completed!")

if __name__ == "__main__":
    main()
