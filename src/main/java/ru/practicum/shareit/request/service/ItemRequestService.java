package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getItemRequestById(Long userId, Long requestId);

    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId, Pageable pageable);
}
