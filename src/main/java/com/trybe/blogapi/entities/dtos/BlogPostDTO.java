package com.trybe.blogapi.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BlogPostDTO implements Serializable {

    private Long id;
    private LocalDateTime published;
    private LocalDateTime updated;
    private String title;
    private String content;
    private UserDTO userDTO;
}
