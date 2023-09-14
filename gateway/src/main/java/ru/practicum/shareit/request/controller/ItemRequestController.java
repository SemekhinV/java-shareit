package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @Validated
    @PostMapping()
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(value = HEADER_SHARER_USER_ID) Long userId,
            @RequestBody @Valid ItemRequestDto itemRequestDto
    ) {

        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemRequests(@RequestHeader(value = HEADER_SHARER_USER_ID) Long userId) {

        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader(value = HEADER_SHARER_USER_ID) Long userId,
            @PathVariable Long requestId
    ) {

        return itemRequestClient.getItemRequest(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader(required = false, value = HEADER_SHARER_USER_ID) Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "20") @Positive Integer size
    ) {

        return itemRequestClient.getAllItemRequests(from, size, userId);
    }
}
