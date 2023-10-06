package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getUser(Long id);

    UserDto addUser(UserDto user);

    UserDto updateUser(UserDto user, Long userId);

    void deleteUser(Long id);
}
