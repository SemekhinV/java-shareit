package ru.practicum.shareit.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(final ConstraintViolationException e) {

        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(final IllegalArgumentException e) {

        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(final MethodArgumentNotValidException e) {

        return new ErrorResponse(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Throwable e) {

        return new ErrorResponse(e.getMessage());
    }
}
