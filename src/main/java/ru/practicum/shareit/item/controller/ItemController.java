package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final ItemRequestService requestService;

    @PostMapping()
    public ItemDto addItem(
            @RequestBody ItemDto item,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {

        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestBody  ItemDto item,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {

        item.setId(itemId);
        return itemService.updateItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingAndComment getItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId
    ) {

        return itemService.getItem(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getAllUsersItems(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemService.getAllUsersItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItems(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestParam String text) {
        return itemService.searchForItems(userId, text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto
    ) {

        return itemService.addComment(commentDto, itemId, userId);
    }
}
