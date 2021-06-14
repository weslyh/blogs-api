package com.trybe.blogapi.entities.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserRequest implements Serializable {

    @Size(min = 8, message = "\"displayName\" lengh must be at least 8 characters long")
    private String displayName;

    @NotNull(message = "\"email\" is required")
    @Email(message = "\"email\" must be a valid email")
    private String email;

    @NotNull(message = "\"password\" is required")
    @Size(min = 6, message = "\"password\" lenght must be 6 characteres long")
    @ToString.Exclude
    private String password;

    private String image;
}
