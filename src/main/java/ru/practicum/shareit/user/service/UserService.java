package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

public interface UserService {
    User getUser(Long id);

    User addUser(User user);

    User updateUser(User user);

    User deleteUser(Long id);
}
