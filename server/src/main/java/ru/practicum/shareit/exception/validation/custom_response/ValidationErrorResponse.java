package ru.practicum.shareit.exception.validation.custom_response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {

    private final List<ErrorResponse> errorResponseList;
}
