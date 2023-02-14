package ru.practicum.shareit.item.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.UserDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public UserDto postItem(
            @RequestBody @Valid UserDto item,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public UserDto patchItem(
            @PathVariable @NonNull Long itemId,
            @RequestBody @Valid UserDto item,
            @RequestHeader(value = "X-Sharer-User-Id") @NonNull Long userId) {

        item.setId(itemId);
        return itemService.updateItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public UserDto getItem(@PathVariable Long itemId) {

        return itemService.getItem(itemId);
    }
}
