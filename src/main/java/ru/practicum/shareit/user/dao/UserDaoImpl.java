package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final HashMap<Long, User> userHashMap;

    @Override
    public Optional<User> getUser(Long id) {

        return Optional.of(userHashMap.get(id));
    }

    @Override
    public User addUser(User user) {

        userHashMap.put(user.getId(), user);

        return user;
    }

    @Override
    public User deleteUser(Long id) {

        return userHashMap.remove(id);
    }
}
