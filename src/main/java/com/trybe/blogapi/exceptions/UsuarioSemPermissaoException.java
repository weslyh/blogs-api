package com.trybe.blogapi.exceptions;

public class UsuarioSemPermissaoException extends RuntimeException {

    public UsuarioSemPermissaoException(String message) {
        super(message);
    }
}
