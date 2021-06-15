package com.trybe.blogapi.entities.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {

    private Long id;
    private String displayName;
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;
}
