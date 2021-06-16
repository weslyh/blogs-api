package com.trybe.blogapi.entities.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse implements Serializable {

    private String message;
    private List<String> messages;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(List<String> messages) {
        this.messages = messages;
    }
}
