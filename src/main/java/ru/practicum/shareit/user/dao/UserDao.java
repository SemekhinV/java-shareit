package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    User getUser(Long id);

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    List<User> getAll();

}
