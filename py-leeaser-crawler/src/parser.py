import json
from pathlib import Path
from bs4 import BeautifulSoup

def parse_page(url: str, html: str) -> dict:
    try:
        soup = BeautifulSoup(html, "lxml")
        blob = url.split("/")[-1]
        data = {
            "title": blob.replace("-", " ").title(),
            "url": url,
            "author": None,
            "genres": [],
            "tags": [],
            "status": None,
            "description": None,
            "cover_url": f"https://novelbin.com/media/novel/{blob}.jpg",
        }
        info_items = soup.select("ul.info.info-meta li")
        for li in info_items:
            header = li.select_one("h3")
            if not header:
                continue
            header_text = header.get_text(strip=True).lower()
            if "author" in header_text:
                a = li.select_one("a")
                if a:
                    data["author"] = a.get_text(strip=True)
            elif "genre" in header_text:
                a_tags = li.select("a")
                data["genres"] = [a.get_text(strip=True) for a in a_tags]
            elif "status" in header_text:
                a = li.select_one("a")
                if a:
                    data["status"] = a.get_text(strip=True)
            elif "tag" in header_text:
                a_tags = li.select("a")
                data["tags"] = [a.get_text(strip=True) for a in a_tags]
        desc_elements = soup.select(".desc-text")
        if desc_elements:
            desc_container = desc_elements[0]
            p_tags = desc_container.find_all("p")
            if p_tags:
                description_text = "\n".join(p.get_text(" ", strip=True) for p in p_tags if p.get_text(strip=True))
                if description_text:
                    data["description"] = description_text
            else:
                for br in desc_container.find_all("br"):
                    br.replace_with("\n")
                description_text = desc_container.get_text(" ", strip=True)
                if description_text:
                    data["description"] = description_text
        return data
    except Exception as e:
        return {
            "title": url.split("/")[-1].replace("-", " ").title(),
            "url": url,
            "author": None,
            "genres": [],
            "tags": [],
            "status": None,
            "description": None,
            "cover_url": f"https://novelbin.com/media/novel/{url.split('/')[-1]}.jpg",
            "error": f"Parse error: {str(e)}"
        }

def save_results(results):
    output_path = Path("data/results/data.json")
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8") as f:
        f.write("[\n")
        for i, item in enumerate(results):
            json.dump(item, f, ensure_ascii=False)
            if i < len(results) - 1:
                f.write(",\n")
        f.write("\n]")
    successful = len([r for r in results if "error" not in r])
    failed = len([r for r in results if "error" in r])
    print(f"💾 Results saved to: {output_path.absolute()}")
    print(f"📊 Summary: {successful} successful, {failed} failed")