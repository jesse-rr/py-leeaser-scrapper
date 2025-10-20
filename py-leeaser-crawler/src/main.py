from parser import save_results
from typing import List
from pathlib import Path
from crawler import run_crawler

def chunkify(lst: List[str], n: int) -> List[List[str]]:
    """Split list into n roughly equal chunks."""
    if n <= 0:
        return [lst]
    k = max(1, len(lst) // n)
    return [lst[i:i + k] for i in range(0, len(lst), k)]

def main():
    links_file = Path("data/links.txt")
    if not links_file.exists():
        print("ERROR: data/links.txt : FILE DOES NOT EXIST")
        return

    urls = [line.strip() for line in links_file.read_text().splitlines() if line.strip()]
    print(f"🕷️  Loaded {len(urls)} URLs to crawl.")    
    results = run_crawler(urls, max_workers=5)

    save_results(results)
    print("✅ Crawling completed!")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n CRAWLER INTERRUPTED")
    except Exception as e:
        print(f"💥 Fatal error: {e}")