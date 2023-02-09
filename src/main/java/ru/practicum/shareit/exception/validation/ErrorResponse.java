package ru.practicum.shareit.exception.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String fieldName;
    private final String message;

    public ErrorResponse (String message) {
        this.fieldName = "";
        this.message = message;
    }
}

