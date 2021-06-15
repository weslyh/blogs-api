package com.trybe.blogapi.entities.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserRequest extends AuthenticationRequest implements Serializable {

    @Size(min = 8, message = "\"displayName\" lengh must be at least 8 characters long")
    private String displayName;
    private String image;
}
