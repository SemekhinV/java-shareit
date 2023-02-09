package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long userId;

    String userLogin;

    String userEmail;
}
