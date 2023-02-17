package ru.practicum.shareit.item.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto postItem(
            @RequestBody @Valid ItemDto item,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(
            @PathVariable @NonNull Long itemId,
            @RequestBody @Valid ItemDto item,
            @RequestHeader(value = "X-Sharer-User-Id") @NonNull Long userId) {

        item.setId(itemId);
        return itemService.updateItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {

        return itemService.getItem(itemId);
    }
}
