package com.example.leeaserscrapperv2.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "sources")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Source {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sourceId;

    private String name;
    private String url;

    @ElementCollection
    private Set<String> externalUrls;
}
