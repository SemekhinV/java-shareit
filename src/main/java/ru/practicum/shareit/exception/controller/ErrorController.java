package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.exception.validation.BadInputParametersException;
import ru.practicum.shareit.exception.validation.EntityExistException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
import ru.practicum.shareit.exception.validation.custom_response.ErrorResponse;
import ru.practicum.shareit.exception.validation.custom_response.ValidationErrorResponse;
import ru.practicum.shareit.exception.validation.EntityAlreadyExistException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorController {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse constraintValidationException(
            final ConstraintViolationException e
    ) {
        final List<ErrorResponse> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new ErrorResponse(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());

        log.error("Ошибка валидации: {}", violations);

        return new ValidationErrorResponse(violations);
    }

    //Обратотка отсутсвия объекта
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityExistExceptionHandle(final EntityExistException e) {
        log.error("Ошибка при попытке обращения к объекту: " + e.getMessage());
        return new ErrorResponse("Ошибка обращения к объекту: ".concat(e.getMessage()));
    }

    //Обработка исключения
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidValueExceptionHandle(final InvalidValueException e) {
        log.error("Ошибка обработки, вызванная некоректными данными: " + e.getMessage());
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

}