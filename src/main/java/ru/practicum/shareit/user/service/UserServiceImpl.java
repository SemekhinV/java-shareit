package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.validation.BadInputParametersException;
import ru.practicum.shareit.exception.validation.EntityAlreadyExistException;
import ru.practicum.shareit.exception.validation.EntityExistException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final UserDao userDao;

    private User isAddValid(UserDto user) {

        if (user.getEmail() == null || user.getName() == null) {
            throw new InvalidValueException("Ошибка создания пользователя, некоторые данные не указаны.");
        }

        if (!user.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            throw new InvalidValueException("Неверно указан email-адрес.");
        }

        if (userDao.getAll().stream().anyMatch(reqUser -> user.getEmail().equals(reqUser.getEmail()))) {
            throw new EntityAlreadyExistException("данный email-адрес уже сушествует.");
        }

        return UserMapper.fromDtoToUser(user);
    }

    private User isUpdateValid(Long userId, UserDto user) {

        if (userId == null) {
            throw new BadInputParametersException("Указан неверный id пользователя.");
        }

        if (userDao.getAll().stream().noneMatch(updatedUser -> userId.equals(updatedUser.getId()))) {
            throw new EntityExistException("Ошибка поиска пользователя, " + "запись с id = " + userId + " не найдена.");
        }

        User updatingUser = userDao.getUser(userId).get();

        if (user.getName() != null) {

            updatingUser.setLogin(user.getName());
        }

        if (user.getEmail() != null) {

            if (user.getEmail().equals(updatingUser.getEmail())) {

                updatingUser.setEmail(user.getEmail());

            } else {

                if (userDao.getAll().stream().anyMatch(reqUser -> user.getEmail().equals(reqUser.getEmail()))) {
                    throw new EntityAlreadyExistException("данный email-адрес уже сушествует.");
                }
            }

            updatingUser.setEmail(user.getEmail());
        }

        return updatingUser;
    }

    @Override
    public List<UserDto> getAll() {

        return userDao
                .getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long id) {

        if (id == null) {
            throw new BadInputParametersException("Указан неверный id пользователя.");
        }

        if (userDao.getAll().stream().anyMatch(user -> id.equals(user.getId()))) {
            return UserMapper.toUserDto(userDao.getUser(id).get());
        } else {
            throw new EntityExistException("Ошибка поиска пользователя, " +
                    "запись с id = " + id + " не найдена.");
        }
    }

    @Override
    public UserDto addUser(UserDto user) {

        User validUser = isAddValid(user);

        return UserMapper.toUserDto(userDao.addUser(validUser));
    }

    @Override
    public UserDto updateUser(UserDto user, Long userId) {

        User fromDb = isUpdateValid(userId, user);

        return UserMapper.toUserDto(userDao.updateUser(fromDb));
    }

    @Override
    public void deleteUser(Long id) {

        if (userDao.getAll().stream().anyMatch(user -> id.equals(user.getId()))) {
            userDao.deleteUser(id);
        } else {
            throw new EntityExistException("Ошибка поиска пользователя, " +
                    "запись с id = " + id + " не найдена.");
        }

    }
}
