package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long requestId
    ) {

        return requestService.getItemRequestById(userId, requestId);
    }

    @PostMapping()
    public ItemRequestDto addItemRequest(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestBody ItemRequestDto itemRequestDto
    ) {

        return requestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping()
    public List<ItemRequestDto> getAllItemRequests(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId
    ) {

        return requestService.getAllItemRequests(userId);
    }
}
