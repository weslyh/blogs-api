package com.trybe.blogapi.exceptions;

public class TokenException extends RuntimeException {

    public TokenException(String message) {
        super(message);
    }
}
