package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto getUser(Long id);

    UserDto addUser(UserDto user);

    UserDto updateUser(UserDto user, Long userId);

    UserDto deleteUser(Long id);
}
