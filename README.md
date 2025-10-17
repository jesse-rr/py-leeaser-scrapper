## Py-Leeaser-Scrapper — Functionality Checklist

- [ ] **Async HTTP core**
Uses `asyncio` with an async HTTP client for non-blocking concurrent requests.

- [ ] **Basic HTML/JSON parser**
Lightweight parsing support for HTML (e.g., BeautifulSoup) and JSON data.

- [ ] **Headless browser (optional)**
Chromium-based browser support (e.g., Playwright) for dynamic content rendering.

- [ ] **Session & cookie management**
Handles cookies and maintains sessions across multiple requests.

- [ ] **User-Agent & header rotation**
Rotates User-Agent strings and headers to mimic real browsers and reduce fingerprinting.

- [ ] **Proxy support**
Allows usage of static or rotating proxy lists (HTTP, SOCKS, etc.).

- [ ] **Request throttling / rate limits**
Adds delay or concurrency caps to avoid triggering anti-bot defenses.

- [ ] **Basic anti-blocking / resilience**
Retries, backoff logic, randomized behavior, and minimal fingerprinting.

- [ ] **CAPTCHA handling (3rd-party)**
Integration-ready for external CAPTCHA solvers like 2Captcha or CapMonster.

- [ ] **Logging & error handling**
Simple logging with retry tracking, error messages, and debug info.

- [ ] **Config file**
Supports JSON or YAML configuration for scraper settings and targets.
