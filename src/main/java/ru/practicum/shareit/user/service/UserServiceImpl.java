package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.validation.EntityExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.mapper.UserMapperImpl;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final UserMapperImpl userMapper;

    private final UserDao userDao;

    public User getUser(Long id) {

        //isIdValid(id, -101);

        return userDao.getUser(id).orElseThrow(
                () -> {throw new EntityExistException("Ошибка поиска юзера, запись с id = " + id + " не найдена.");}
        );
    }

    @Override
    public User addUser(UserDto user) {



    }

    @Override
    public User updateUser(UserDto user) {
        return null;
    }

    @Override
    public User deleteUser(Long id) {
        return null;
    }
}
