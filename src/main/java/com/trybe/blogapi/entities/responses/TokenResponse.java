package com.trybe.blogapi.entities.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse implements Serializable {

    private String token =  "Token qualquer !";
}
