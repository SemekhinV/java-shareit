package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    User getUser(Long id);

    User addUser(UserDto user);

    User updateUser(UserDto user);

    User deleteUser(Long id);
}
