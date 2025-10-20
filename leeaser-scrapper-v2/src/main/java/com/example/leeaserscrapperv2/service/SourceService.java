package com.example.leeaserscrapperv2.service;

import com.example.leeaserscrapperv2.model.Source;
import com.example.leeaserscrapperv2.repository.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;

    public void setSourceExternalUrls(Set<String> links, String name) {
        Source source = this.getSourceByName(name);
        source.getExternalUrls().addAll(links);
        sourceRepository.save(source);
    }

    protected Source getSourceByName(String name) {
        return sourceRepository.findByName(name);
    }

    public List<String> getAllExternalUrls(String name) {
        return sourceRepository.findAllExternalUrlsBySourceName(name);
    }
}
