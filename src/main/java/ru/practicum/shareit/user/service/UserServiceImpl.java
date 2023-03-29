package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.validation.*;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements  UserService{

    private final UserRepository userRepository;

    private void isValid(UserDto user) {

        if (user.getEmail() == null || user.getName() == null) {
            throw new InvalidValueException("Ошибка создания пользователя, некоторые данные не указаны.");
        }

        if (!user.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            throw new InvalidValueException("Неверно указан email-адрес.");
        }
    }

    @Override
    public List<UserDto> getAll() {

        return userRepository
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto user) {

        isValid(user);

        try {
            return toUserDto(userRepository.save(toUser(user)));
        } catch (DataIntegrityViolationException e) {
            //Т.к. при создании поля email в таблице мы указали уникальность этого поля, будет выброшено
            //Низкоуровневое исключение ConstraintViolationException, которое повлечет за собой вызов
            //Другого исключения, а именно DataIntegrityViolationException
            //Ранее данное исключение отлавливалось в контроллере исключений, но теперь необходимо индивидуальное
            //Сообщение
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new InvalidEmailException("Пользователь с почтой " + user.getEmail() + " уже существует.");
            }
        }

        return null;
    }

    @Override
    @Transactional
    public UserDto getUser(Long id) {

        if (id == null) {
            throw new BadInputParametersException("Указан неверный id пользователя.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new EntityNotFoundException("Ошибка поиска пользователя, " +
                    "запись с id = " + id + " не найдена.");
        });

        return toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto user, Long userId) {

        User toUpdate = userRepository.findById(userId).orElseThrow(() -> {
            throw new EntityNotFoundException("Пользователь с id = " + userId + " не найден");
        });

        if (user.getName() != null) {toUpdate.setName(user.getName());}
        if (user.getEmail() != null) {toUpdate.setEmail(user.getEmail());}

        try {
            return toUserDto(userRepository.save(toUpdate));
        }  catch (DataIntegrityViolationException e) {

            if (e.getCause() instanceof ConstraintViolationException) {
                throw new InvalidEmailException("Пользователь с почтой " + user.getEmail() + " уже существует.");
            }
        }

        return null;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        if (id == null) {throw new BadInputParametersException("Значения поле id не может быть пустым.");}

        try {
            userRepository.deleteById(id);
        } catch (OptimisticLockingFailureException e) {
            throw new EntityNotFoundException("Ошибка удаления, сущность с id = " + id + " не найдена.");
        }
    }
}
