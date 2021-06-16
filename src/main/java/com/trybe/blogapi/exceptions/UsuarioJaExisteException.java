package com.trybe.blogapi.exceptions;

public class UsuarioJaExisteException extends RuntimeException {

    public UsuarioJaExisteException(String message) {
        super(message);
    }
}
