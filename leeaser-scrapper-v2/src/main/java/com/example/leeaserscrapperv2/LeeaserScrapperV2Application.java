package com.example.leeaserscrapperv2;

import com.example.leeaserscrapperv2.service.NovelBin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LeeaserScrapperV2Application implements CommandLineRunner {

    private final NovelBin novelBin;

    public LeeaserScrapperV2Application(NovelBin novelBin) {
        this.novelBin = novelBin;
    }

    public static void main(String[] args) {
        SpringApplication.run(LeeaserScrapperV2Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        novelBin.runParallelScrape();
    }
}
