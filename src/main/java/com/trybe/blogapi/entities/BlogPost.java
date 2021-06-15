package com.trybe.blogapi.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Posts")
@NoArgsConstructor
@Data
public class BlogPost {

    public BlogPost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private LocalDateTime published;
    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @PrePersist
    public void prePersist() {
        this.published = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}
