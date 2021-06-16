package com.trybe.blogapi.repositories;

import com.trybe.blogapi.entities.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    @Query("SELECT bp FROM BlogPost bp WHERE bp.title LIKE %:search% OR bp.content LIKE %:search%")
    List<BlogPost> findAllByTitleOrContentContaining(String search);
}
