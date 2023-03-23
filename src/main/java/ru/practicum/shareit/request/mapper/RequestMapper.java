package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class RequestMapper {

    public static ItemRequest toRequest(ItemRequestDto itemRequestDto) {

        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .userId(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .items(List.of())
                .build();
    }

}
