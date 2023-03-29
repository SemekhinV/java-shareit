package ru.practicum.shareit.exception.validation;

public class ItemUnavailableException extends RuntimeException {

    public ItemUnavailableException(String message) {
        super(message);
    }
}
