package com.trybe.blogapi.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

@Component
public interface JwtService {

    String geraToken(String email);
    boolean validaToken(String token);
    DecodedJWT decodeToken(String token);
}
