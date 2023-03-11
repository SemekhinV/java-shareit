package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto postUser(@RequestBody UserDto user) {

        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto patchItem(
            @PathVariable Long userId,
            @RequestBody UserDto user) {

        user.setId(userId);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {

        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping()
    public List<UserDto> getAll() {

        return userService.getAll();
    }
}
