package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getSingleItemRequest(Long userId, Long requestId);

    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId);
}
