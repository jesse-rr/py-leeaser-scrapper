package com.example.leeaserscrapperv2.model;

import com.example.leeaserscrapperv2.model.helper.BaseEntity;
import com.example.leeaserscrapperv2.model.helper.PublicationGenre;
import com.example.leeaserscrapperv2.model.helper.PublicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "publications")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Publication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publicationId;

    @Column(columnDefinition = "TEXT")
    private String title;
    @Column(columnDefinition = "TEXT")
    private String author;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String coverImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublicationStatus status;


    @Min(0) @Max(5)
    private double totalRating = 0;
    @Column(nullable = false)
    private int totalViews = 0;
    @Column(nullable = false)
    private int totalFavorites = 0;
    @Column(nullable = false)
    private int totalBookmarks = 0;
    @Column(nullable = false)
    private int totalRead_later = 0;


    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "publication_tags",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "publication_genres", joinColumns = @JoinColumn(name = "publication_id"))
    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<PublicationGenre> genres = new HashSet<>();

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Chapter> chapters = new HashSet<>();

}
