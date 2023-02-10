package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserDao {

    Optional<User> getUser(Long id);

    User addUser(User user);

    User deleteUser(Long id);
}
