package com.trybe.blogapi.entities.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class BlogPostRequest implements Serializable {

    @NotEmpty(message = "\"title\" is not allowed to be empty")
    @NotNull(message = "\"title\" is required")
    private String title;

    @NotEmpty(message = "\"title\" is not allowed to be empty")
    @NotNull(message = "\"content\" is required")
    private String content;
}
