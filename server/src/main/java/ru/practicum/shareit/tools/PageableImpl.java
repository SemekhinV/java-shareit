package ru.practicum.shareit.tools;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.validation.InvalidValueException;

public class PageableImpl {

    public static PageRequest of(Integer from, Integer size, Sort sort) {

        if (size == null || from == null) {

            return PageRequest.of(0, 20, sort);
        }

        if (size <= 0 || from < 0) throw new InvalidValueException("Переданные значение должны быть больше нуля.");

        return PageRequest.of(from / size, size, sort);
    }
}