package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public static UserDto toUserDto(User user) {

        return UserDto.builder()
                .userId(user.getId())
                .userLogin(user.getLogin())
                .userEmail(user.getEmail())
                .build();
    }

    public static User fromDtoToUser(UserDto userDto) {

        return User.builder()
                .id(userDto.getUserId())
                .login(userDto.getUserLogin())
                .email(userDto.getUserEmail())
                .build();
    }
}
