package com.example.leeaserscrapperv2.model;

import com.example.leeaserscrapperv2.model.helper.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chapters")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chapter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chapterId;

    @Column(columnDefinition = "TEXT")
    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private double number;
    @Column(nullable = false)
    private int views = 0;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;
}
