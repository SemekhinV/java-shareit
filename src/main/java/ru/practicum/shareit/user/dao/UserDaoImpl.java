package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final HashMap<Long, User> userHashMap;

    private Long globalId = 0L;

    @Override
    public User getUser(Long id) {

        return userHashMap.get(id);
    }

    @Override
    public User addUser(User user) {

        globalId++;

        user.setId(globalId);

        userHashMap.put(globalId, user);

        return user;
    }

    @Override
    public User updateUser(User user) {

        userHashMap.put(user.getId(), user);

        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userHashMap.remove(id);
    }

    @Override
    public List<User> getAll() {

        return new ArrayList<>(userHashMap.values());
    }

}
