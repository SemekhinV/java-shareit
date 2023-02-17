package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.validation.EntityAlreadyExistException;
import ru.practicum.shareit.exception.validation.EntityExistException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final UserDao userDao;

    private User isValid(UserDto user) {

        if (user.getEmail() == null
                || !user.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        ) {
            throw new InvalidValueException("Ошибка создания пользователя, неверно введен email.");
        }

        if (userDao.getAll()
                .stream()
                .anyMatch(usr -> usr.getEmail().equals(user.getEmail()))
        ) {
            throw new EntityAlreadyExistException("Выбранный email уже занят.");
        }

        return UserMapper.fromDtoToUser(user);
    }

    public UserDto getUser(Long id) {

        return UserMapper.toUserDto(
                userDao.getUser(id).orElseThrow(
                        () -> {
                            throw new EntityExistException("Ошибка поиска юзера, " +
                                    "запись с id = " + id + " не найдена.");
                        })
        );
    }

    @Override
    public UserDto addUser(@Valid UserDto user) {

        User validUser = isValid(user);

        validUser.setId((long) (userDao.getAll().size() + 1));

        return UserMapper.toUserDto(userDao.addUser(validUser));
    }

    @Override
    public UserDto updateUser(UserDto user, Long userId) {

        if (userDao.getAll().stream().anyMatch(compUser ->
            compUser.getEmail().equals(user.getEmail()))) {
            throw new EntityExistException("Ошибка обновления пользователя, введенный email уже занят.");
        }

        User updatingUser = userDao.getUser(userId).orElseThrow(
                () -> {
                    throw new EntityExistException("Ошибка обновления данных пользователя, указанный индекс не найден.");
                }
        );

        if (user.getEmail() != null) {
            updatingUser.setEmail(user.getEmail());
        } if (user.getName() != null) {
            updatingUser.setLogin(user.getName());
        }

        return UserMapper.toUserDto(userDao.addUser(updatingUser));
    }

    @Override
    public UserDto deleteUser(Long id) {

        userDao.getUser(id).orElseThrow(
                () -> {
                    throw new EntityExistException("Ошибка удаления пользователя, указанный id не найден");
                }
        );

        return UserMapper.toUserDto(userDao.deleteUser(id));
    }
}
