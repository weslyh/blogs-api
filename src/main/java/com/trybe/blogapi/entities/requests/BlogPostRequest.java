package com.trybe.blogapi.entities.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class BlogPostRequest implements Serializable {

    @NotNull(message = "\"title\" is required")
    private String title;

    @NotNull(message = "\"content\" is required")
    private String content;
}
