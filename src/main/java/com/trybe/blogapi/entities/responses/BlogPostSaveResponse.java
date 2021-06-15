package com.trybe.blogapi.entities.responses;

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
}
