package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String error;

    public ErrorResponse(String description) {
        this.error = description;
    }

}
