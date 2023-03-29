package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.exception.validation.*;
import ru.practicum.shareit.exception.validation.custom_response.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ErrorController {


    //Обратотка отсутсвия объекта
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityExistExceptionHandle(final EntityNotFoundException e) {
        log.error("Ошибка при попытке обращения к объекту: " + e.getMessage());
        return new ErrorResponse("Ошибка обращения к объекту: ".concat(e.getMessage()));
    }

    //Обработка исключения
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidValueExceptionHandle(final InvalidValueException e) {
        log.error("Ошибка обработки, вызванная некорректными данными: " + e.getMessage());
        return new ErrorResponse("Ошибка при обработке некорректных данных: ".concat(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse recordAlreadyExist(final EntityAlreadyExistException e) {
        log.error("Конфликт при записи данных в хранилище: ".concat(e.getMessage()));
        return new ErrorResponse("Конфликт при добавлении записи: ".concat(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse badInputParameters(final BadInputParametersException e) {
        log.error("При получении запроса были приняты некорректные входные параметры ".concat(e.getMessage()));
        return new ErrorResponse("Ошибка значений во входных параметрах запроса ".concat(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse permissionDenied(final PermissionDeniedException e) {
        log.error("Отказ в доступе: ".concat(e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse itemUnavailable(final ItemUnavailableException e) {
        log.error("Ошибка бронирования: ".concat(e.getMessage()));
        return new ErrorResponse("Ошибка бронирования: ".concat(e.getMessage()));
    }

}