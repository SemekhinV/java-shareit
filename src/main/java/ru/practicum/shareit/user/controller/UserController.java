package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    private static final String USER_ERROR_MESSAGE = "Переадно пустое значение id пользователя";

    @PostMapping()
    public UserDto postUser(@RequestBody UserDto user) {

        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto patchItem(
            @PathVariable @NotNull(message = USER_ERROR_MESSAGE) Long userId,
            @RequestBody UserDto user
    ) {

        user.setId(userId);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @NotNull(message = USER_ERROR_MESSAGE) Long userId) {

        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @NotNull(message = USER_ERROR_MESSAGE) Long userId) {

        userService.deleteUser(userId);
    }

    @GetMapping()
    public List<UserDto> getAll() {

        return userService.getAll();
    }
}
