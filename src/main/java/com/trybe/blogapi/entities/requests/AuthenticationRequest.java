package com.trybe.blogapi.entities.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class AuthenticationRequest {

    @NotNull(message = "\"email\" is required")
    @NotEmpty(message = "\"email\" is not allowed to be empty")
    @Email(message = "\"email\" must be a valid email")
    private String email;

    @NotNull(message = "\"password\" is required")
    @NotEmpty(message = "\"password\" is not allowed to be empty")
    @Size(min = 6, message = "\"password\" lenght must be 6 characteres long")
    @ToString.Exclude
    private String password;
}
