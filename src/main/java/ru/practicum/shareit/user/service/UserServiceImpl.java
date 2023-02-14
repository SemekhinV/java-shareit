package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.validation.EntityAlreadyExistException;
import ru.practicum.shareit.exception.validation.EntityExistException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final UserMapper userMapper;

    private final UserDao userDao;

    private User isValid(UserDto user) {

        if (userDao.getAll()
                .stream()
                .anyMatch(usr -> usr.getEmail().equals(user.getUserEmail()))
        ) {
            throw new EntityAlreadyExistException("Выбраный email уже кем-то занят.");
        }

        return UserMapper.fromDtoToUser(user);
    }

    public UserDto getUser(Long id) {

        return UserMapper.toUserDto(
                userDao.getUser(id).orElseThrow(
                        () -> {throw new EntityExistException("Ошибка поиска юзера, " +
                                "запись с id = " + id + " не найдена.");
                        })
        );
    }

    @Override
    public UserDto addUser(UserDto user) {

        User validUser = isValid(user);

        return UserMapper.toUserDto(userDao.addUser(validUser));
    }

    @Override
    public UserDto updateUser(UserDto user, Long userId) {
        return null;
    }

    @Override
    public UserDto deleteUser(Long id) {
        return null;
    }
}
