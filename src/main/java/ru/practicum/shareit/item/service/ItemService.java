package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.UserDto;

public interface ItemService {

    UserDto getItem(Long id);

    UserDto addItem(Long userId, UserDto item);

    UserDto updateItem(UserDto item, Long userId);
}
