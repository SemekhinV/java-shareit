package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.tools.PageableImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final ItemRequestService requestService;

    private static final String USER_ID = "X-Sharer-User-Id";

    private static final String ITEM_ID_ERROR = "Передано пустое значение id вещи";

    private static final String USER_ID_ERROR = "Передано пустое значение id пользователя";

    @PostMapping()
    public ItemDto addItem(
            @RequestBody ItemDto item,
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ID_ERROR) Long userId
    ) {

        ItemRequestDto itemRequest = (item.getRequestId() != null) ?
                requestService.getItemRequestById(userId, item.getRequestId()) : null;

        return itemService.addItem(userId, item, itemRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable @NotNull(message = ITEM_ID_ERROR) Long itemId,
            @RequestBody ItemDto item,
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ID_ERROR) Long userId
    ) {

        item.setId(itemId);
        return itemService.updateItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingAndComment getItem(
            @PathVariable @NotNull(message = ITEM_ID_ERROR) Long itemId,
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ID_ERROR) Long userId
    ) {

        return itemService.getItem(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getAllUsersItems(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ID_ERROR) Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {

        return itemService.getAllUsersItems(userId, PageableImpl.of(from, size, Sort.by("id").ascending()));
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItems(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ID_ERROR) Long userId,
            @RequestParam String text,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {

        return itemService.searchForItems(
                userId, text, PageableImpl.of(from, size, Sort.by("id").ascending())
        );
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ID_ERROR) Long userId,
            @PathVariable @NotNull(message = "id вещи не может быть пустым.") Long itemId,
            @RequestBody @Valid CommentDto commentDto
    ) {

        return itemService.addComment(commentDto, itemId, userId);
    }
}
