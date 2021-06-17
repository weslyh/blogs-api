package com.trybe.blogapi.entities.responses;

import com.trybe.blogapi.entities.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostSaveResponse implements Serializable {

    private String title;
    private String content;
    private Long userId;

    public BlogPostSaveResponse(BlogPost blogPost) {
        this.title = blogPost.getTitle();
        this.content = blogPost.getContent();
        this.userId = blogPost.getUser().getId();
    }
}
