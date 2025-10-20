package com.example.leeaserscrapperv2.repository;

import com.example.leeaserscrapperv2.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {

    Source findByName(String name);

    @Query("SELECT s.externalUrls FROM Source s WHERE s.name = :name")
    List<String> findAllExternalUrlsBySourceName(String name);
}
