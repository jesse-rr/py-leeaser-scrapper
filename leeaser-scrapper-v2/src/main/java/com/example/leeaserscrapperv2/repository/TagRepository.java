package com.example.leeaserscrapperv2.repository;

import com.example.leeaserscrapperv2.model.Tag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Transactional
    @Query(
            value = "INSERT INTO tags(name) VALUES (:name) ON CONFLICT (name) DO UPDATE SET name=EXCLUDED.name RETURNING *",
            nativeQuery = true
    )
    Tag upsertTag(@Param("name") String name);
}
