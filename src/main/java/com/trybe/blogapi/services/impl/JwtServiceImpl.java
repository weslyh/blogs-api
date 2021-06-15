package com.trybe.blogapi.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.trybe.blogapi.services.JwtService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtServiceImpl implements JwtService {

    private static final String key = "trybe";

    @Override
    public String geraToken(String email) {
        Algorithm algorithm = Algorithm.HMAC256(key);

        return JWT.create()
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)))
                .sign(algorithm);
    }

    @Override
    public boolean validaToken(String token) {
        if (token == null || token.isEmpty())
            throw new RuntimeException("");

        decodeToken(token);

        return true;
    }

    @Override
    public DecodedJWT decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(key);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaimPresence("email")
                    .build();

            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token Invalido");
        }
    }
}
