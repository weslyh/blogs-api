package com.trybe.blogapi.exceptions;

import com.trybe.blogapi.entities.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException notFoundException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(notFoundException.getMessage()));
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorResponse> handleTokenException(TokenException tokenException) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(tokenException.getMessage()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException missingRequestHeaderException) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Token não encontrado"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult result = methodArgumentNotValidException.getBindingResult();
        List<String> errors = processFieldErrorMessages(result.getFieldErrors());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors.size() == 1 ? new ErrorResponse(errors.get(0)) : new ErrorResponse(errors));
    }

    @ExceptionHandler(UsuarioSemPermissaoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioSemPermissaoException(UsuarioSemPermissaoException usuarioSemPermissaoException) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(usuarioSemPermissaoException.getMessage()));
    }

    @ExceptionHandler(UsuarioJaExisteException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioJaExisteException(UsuarioJaExisteException usuarioJaExisteException) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(usuarioJaExisteException.getMessage()));
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ErrorResponse> handleLoginException(LoginException loginException) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(loginException.getMessage()));
    }

    private List<String> processFieldErrorMessages(List<FieldError> fieldErrors) {
        return fieldErrors
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
    }
}
