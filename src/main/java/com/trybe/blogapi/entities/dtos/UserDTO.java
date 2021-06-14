package com.trybe.blogapi.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserDTO implements Serializable {

    private Long id;
    private String displayName;
    private String email;
    private String image;
}
