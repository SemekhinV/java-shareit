package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.UserDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static UserDto toItemDto(Item item) {

        return UserDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .available(item.getAvailable())
                .build();
    }
}
