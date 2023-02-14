package ru.practicum.shareit.user.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto postUser(@RequestBody @Valid UserDto user) {

        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto patchItem(
            @PathVariable @NonNull Long userId,
            @RequestBody @Valid UserDto user) {

        user.setId(userId);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{itemId}")
    public UserDto getItem(@PathVariable Long itemId) {

        return userService.getUser(itemId);
    }
}
