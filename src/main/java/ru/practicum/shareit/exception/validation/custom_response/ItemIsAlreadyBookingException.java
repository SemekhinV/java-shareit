package ru.practicum.shareit.exception.validation.custom_response;

public class ItemIsAlreadyBookingException extends RuntimeException {

    public ItemIsAlreadyBookingException(String message) {
        super(message);
    }
}
