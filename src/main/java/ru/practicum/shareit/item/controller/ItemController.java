package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto postItem(@RequestBody @Valid ItemDto item,
                            @RequestHeader(value = "X-Sharer-User-Id")
                            Long userId) {

        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable Long itemId, @RequestBody @Valid ItemDto item) {

        return itemService.updateItem(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {

        return itemService.getItem(itemId);
    }
}
