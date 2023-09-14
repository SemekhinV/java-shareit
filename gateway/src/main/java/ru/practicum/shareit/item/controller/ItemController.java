package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.validations.OnCreate;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping()
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createItem(
            @RequestHeader(required = false, value = HEADER_SHARER_USER_ID) Long userId,
            @RequestBody @Valid ItemDto itemDto
    ) {

        return itemClient.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(required = false, value = HEADER_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId
    ) {

        return itemClient.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(required = false, value = HEADER_SHARER_USER_ID) Long userId,
            @RequestBody ItemDto itemDto,
            @PathVariable Long itemId
    ) {

        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {

        itemClient.deleteItem(itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItems(
            @RequestHeader(required = false, value = HEADER_SHARER_USER_ID) Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size
    ) {

        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader(required = false, value = HEADER_SHARER_USER_ID) Long userId,
            @RequestParam(required = false) @NotNull String text,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "20") @Positive Integer size
    ) {

        return itemClient.searchItems(text, userId, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createItemComment(
            @RequestHeader(value = HEADER_SHARER_USER_ID)Long userId,
            @RequestBody @Valid CommentDto commentDto,
            @PathVariable Long itemId
    ) {

        if (userId == null) throw new IllegalArgumentException("Field userId is null");

        return itemClient.createComment(commentDto, itemId, userId);
    }
}
