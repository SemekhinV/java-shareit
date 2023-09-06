package ru.practicum.shareit.tools;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.validation.InvalidValueException;

public class PageRequestImpl {

    public static PageRequest of (Integer size, Integer from, Sort sort) {

        if (size == null || from == null) return null;
        if (size <= 0 || from < 0) throw new InvalidValueException("Переданные значение должны быть больше нуля.");

        return PageRequest.of(from, size, sort); //from/size ?
    }
}
