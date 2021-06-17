package com.trybe.blogapi.entities.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BlogPostDTO implements Serializable {

    private Long id;
    private LocalDateTime published;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updated;
    private String title;
    private String content;
    @JsonProperty("user")
    private UserDTO userDTO;
}
