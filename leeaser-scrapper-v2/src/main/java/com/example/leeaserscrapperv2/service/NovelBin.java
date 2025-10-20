package com.example.leeaserscrapperv2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class NovelBin {

    private final TagService tagService;
    private final PublicationService publicationService;
    private final SourceService sourceService;
    private final Random random = new Random();

    public void runParallelScrape() throws InterruptedException {
        List<String> allUrls = sourceService.getAllExternalUrls("novelbin");
        int batchSize = 8;
        int numThreads = 2;

        System.out.println("Starting scrape of " + allUrls.size() + " URLs");
        System.out.println("Using direct connection with enhanced stealth");

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < allUrls.size(); i += batchSize) {
            List<String> batch = allUrls.subList(i, Math.min(i + batchSize, allUrls.size()));
            final int batchNumber = (i / batchSize) + 1;
            final int totalBatches = (int) Math.ceil((double) allUrls.size() / batchSize);

            futures.add(executor.submit(() -> {
                System.out.println("Starting batch " + batchNumber + "/" + totalBatches + " (" + batch.size() + " URLs)");
                NovelBinSession session = new NovelBinSession(tagService, publicationService);
                session.scrapeBatch(batch);
                session.close();
                System.out.println("Completed batch " + batchNumber + "/" + totalBatches);
            }));

            if (i + batchSize < allUrls.size()) {
                Thread.sleep(3000);
            }
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                System.out.println("Batch failed: " + e.getMessage());
            }
        }

        executor.shutdown();
        executor.awaitTermination(48, TimeUnit.HOURS);
        System.out.println("All batches completed");
    }
}