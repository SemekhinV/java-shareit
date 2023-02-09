package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final List<User> userList;

    @Override
    public Optional<User> getUser(Long id) {
        return Optional.of(userList.get(Math.toIntExact(id)));
    }


}
