package com.example.leeaserscrapperv2.service;

import com.example.leeaserscrapperv2.model.Publication;
import com.example.leeaserscrapperv2.model.Tag;
import com.example.leeaserscrapperv2.model.helper.PublicationGenre;
import com.example.leeaserscrapperv2.model.helper.PublicationStatus;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class NovelBinSession {

    private final WebDriver driver;
    private final Random random = new Random();
    private final TagService tagService;
    private final PublicationService publicationService;

    public NovelBinSession(TagService tagService, PublicationService publicationService) {
        this.tagService = tagService;
        this.publicationService = publicationService;

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        String[] userAgents = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        };

        String userAgent = userAgents[random.nextInt(userAgents.length)];

        options.addArguments(
                "--headless=new",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-blink-features=AutomationControlled",
                "--disable-extensions",
                "--disable-web-security",
                "--disable-logging",
                "--log-level=3",
                "--no-first-run",
                "--disable-default-apps",
                "--disable-popup-blocking",
                "--disable-hang-monitor",
                "--disable-background-timer-throttling",
                "--disable-renderer-backgrounding",
                "--disable-features=TranslateUI,BlinkGenPropertyTrees",
                "--disable-component-extensions-with-background-pages",
                "--disable-client-side-phishing-detection",
                "--disable-cast",
                "--disable-field-trial-config",
                "--disable-ipc-flooding-protection",
                "--disable-backgrounding-occluded-windows",
                "--disable-renderer-backgrounding",
                "--disable-site-isolation-trials",
                "--disable-web-resources",
                "--disable-translate",
                "--disable-background-networking",
                "--disable-sync",
                "--metrics-recording-only",
                "--safebrowsing-disable-auto-update",
                "--password-store=basic",
                "--use-mock-keychain",
                "--window-size=1920,1080",
                "--user-agent=" + userAgent
        );

        options.setExperimentalOption("excludeSwitches", Arrays.asList(
                "enable-automation",
                "enable-logging",
                "disable-background-timer-throttling",
                "disable-component-extensions-with-background-pages"
        ));
        options.setExperimentalOption("useAutomationExtension", false);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        this.driver = new ChromeDriver(options);

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
            js.executeScript("Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]})");
            js.executeScript("Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']})");
            js.executeScript("window.chrome = {runtime: {}};");
            js.executeScript("const originalQuery = window.navigator.permissions.query; return originalQuery.bind(navigator);");
            js.executeScript("Object.defineProperty(navigator, 'permissions', {get: () => ({query: () => Promise.resolve({state: 'granted'})})});");
        } catch (Exception e) {
        }
    }

    public void scrapeBatch(List<String> urls) {
        int successCount = 0;
        int failCount = 0;

        for (String url : urls) {
            boolean success = false;
            int attempts = 0;

            while (!success && attempts < 3) {
                try {
                    System.out.println("Scraping: " + url + " (attempt " + (attempts + 1) + ")");
                    success = scrapeNovelDetails(url);

                    if (success) {
                        successCount++;
                        System.out.println("Success: " + url);
                        Thread.sleep(8000 + random.nextInt(7000));
                    } else {
                        failCount++;
                        System.out.println("Failed: " + url);
                        Thread.sleep(15000 + random.nextInt(10000));
                        attempts++;
                    }
                } catch (Exception e) {
                    System.out.println("Error scraping " + url + ": " + e.getMessage());
                    attempts++;
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        System.out.println("Batch results: " + successCount + " successes, " + failCount + " failures");
    }

    private boolean scrapeNovelDetails(String novelUrl) {
        try {
            System.out.println("Loading page: " + novelUrl);
            driver.get(novelUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.info.info-meta")));
                System.out.println("Found info-meta element");
            } catch (TimeoutException e) {
                System.out.println("Timeout waiting for info-meta element");
                String pageSource = driver.getPageSource();

                if (pageSource.contains("Checking your browser") || pageSource.contains("cloudflare")) {
                    System.out.println("Cloudflare challenge detected");
                    return false;
                }

                if (pageSource.contains("404") || pageSource.contains("Not Found")) {
                    System.out.println("Page not found");
                    return false;
                }

                return false;
            }

            String blob = novelUrl.substring(novelUrl.lastIndexOf("/") + 1);
            String title = Arrays.stream(blob.split("-"))
                    .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                    .collect(Collectors.joining(" "));

            String author = "";
            PublicationStatus status = PublicationStatus.ONGOING;
            Set<PublicationGenre> genres = new HashSet<>();
            Set<String> tagNames = new HashSet<>();
            String imageUrl = String.format("https://novelbin.com/media/novel/%s.jpg", blob);

            try {
                WebElement infoMeta = driver.findElement(By.cssSelector("ul.info.info-meta"));
                List<WebElement> listItems = infoMeta.findElements(By.tagName("li"));

                for (WebElement item : listItems) {
                    String text = item.getText();

                    if (text.contains("Author:")) {
                        try {
                            author = item.findElement(By.tagName("a")).getText().trim();
                        } catch (Exception e) {
                            author = text.replace("Author:", "").trim();
                        }
                    }

                    if (text.contains("Status:")) {
                        try {
                            WebElement statusLink = item.findElement(By.tagName("a"));
                            if (statusLink.getText().equalsIgnoreCase("completed")) status = PublicationStatus.COMPLETED;
                        } catch (Exception e) {
                            if (text.toLowerCase().contains("completed")) status = PublicationStatus.COMPLETED;
                        }
                    }

                    if (text.contains("Genre:")) {
                        List<WebElement> genreLinks = item.findElements(By.tagName("a"));
                        for (WebElement g : genreLinks) {
                            PublicationGenre genre = parsePublicationGenre(g.getText().trim());
                            if (genre != null) genres.add(genre);
                        }
                    }

                    if (text.contains("Tag:")) {
                        List<WebElement> tagLinks = item.findElements(By.tagName("a"));
                        for (WebElement t : tagLinks) {
                            String tagText = t.getText().trim();
                            if (!tagText.isEmpty() && !tagText.equals("See more »")) tagNames.add(tagText);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error parsing metadata");
            }

            StringBuilder descBuilder = new StringBuilder();
            try {
                WebElement descContainer = driver.findElement(By.cssSelector(".desc-text"));
                List<WebElement> paragraphs = descContainer.findElements(By.tagName("p"));
                if (!paragraphs.isEmpty()) {
                    for (WebElement p : paragraphs) {
                        String text = p.getText().trim();
                        if (!text.isEmpty()) descBuilder.append(text).append("\n\n");
                    }
                } else {
                    descBuilder.append(descContainer.getText().trim());
                }
            } catch (Exception e) {
                System.out.println("Error parsing description");
            }

            Set<Tag> resolvedTags = tagService.resolveTags(tagNames);

            Publication publication = Publication.builder()
                    .title(title)
                    .author(author)
                    .status(status)
                    .genres(genres)
                    .tags(resolvedTags)
                    .description(descBuilder.toString().trim())
                    .coverImg(imageUrl)
                    .build();

            publicationService.savePublication(publication);
            System.out.println("Saved: " + title);
            return true;

        } catch (TimeoutException e) {
            System.out.println("Timeout scraping: " + novelUrl);
            return false;
        } catch (Exception e) {
            System.out.println("Error scraping " + novelUrl + ": " + e.getMessage());
            return false;
        }
    }

    private PublicationGenre parsePublicationGenre(String genreText) {
        if (genreText == null || genreText.isEmpty()) return null;
        try {
            return PublicationGenre.valueOf(
                    genreText.trim().toUpperCase().replace(" ", "_").replace("-", "_")
            );
        } catch (Exception e) {
            return null;
        }
    }

    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}