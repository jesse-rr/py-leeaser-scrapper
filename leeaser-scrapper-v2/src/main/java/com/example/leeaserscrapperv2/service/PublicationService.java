package com.example.leeaserscrapperv2.service;

import com.example.leeaserscrapperv2.model.Publication;
import com.example.leeaserscrapperv2.repository.PublicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;

    @Transactional
    public void savePublication(Publication publication) {
        publicationRepository.save(publication);
    }
}
