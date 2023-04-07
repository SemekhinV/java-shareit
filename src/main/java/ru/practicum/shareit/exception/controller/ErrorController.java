package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.exception.validation.*;
import ru.practicum.shareit.exception.validation.custom_response.ErrorResponse;

import java.sql.SQLException;

@RestControllerAdvice
@Slf4j
public class ErrorController {


    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityExistExceptionHandle(final EntityNotFoundException e) {
        log.error("Ошибка при попытке обращения к объекту: " + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidValueExceptionHandle(final InvalidValueException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse recordAlreadyExist(final EmailException e) {
        log.error("Конфликт при записи данных в хранилище: ".concat(e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse badParameters(final BadInputParametersException e) {
        log.error("При получении запроса были приняты некорректные входные параметры ".concat(e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse sqlException(final SQLException e) {
        log.error("Ошибка валидации: ".concat(e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

}