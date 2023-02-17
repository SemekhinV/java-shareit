package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public static UserDto toUserDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .name(user.getLogin())
                .email(user.getEmail())
                .build();
    }

    public static User fromDtoToUser(UserDto userDto) {

        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
