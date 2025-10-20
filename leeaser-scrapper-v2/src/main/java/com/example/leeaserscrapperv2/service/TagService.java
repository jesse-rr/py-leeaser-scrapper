package com.example.leeaserscrapperv2.service;

import com.example.leeaserscrapperv2.model.Tag;
import com.example.leeaserscrapperv2.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public Set<Tag> resolveTags(Set<String> tagNames) {
        Set<Tag> result = new HashSet<>();
        for (String name : tagNames) {
            Tag tag = tagRepository.upsertTag(name);
            result.add(tag);
        }
        return result;
    }
}
