package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.validations.OnCreate;
import ru.practicum.shareit.error.validations.OnUpdate;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping()
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {

        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {

        return userClient.getUser(userId);
    }

    @Validated(OnUpdate.class)
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long userId,
            @RequestBody @Valid UserDto userDto
    ) {

        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {

        userClient.deleteUser(userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {

        return userClient.getUsers();
    }
}
