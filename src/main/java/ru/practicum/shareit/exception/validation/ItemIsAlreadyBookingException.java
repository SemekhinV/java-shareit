package ru.practicum.shareit.exception.validation;

public class ItemIsAlreadyBookingException extends RuntimeException {

    public ItemIsAlreadyBookingException(String message) {
        super(message);
    }
}
