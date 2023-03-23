package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;

import java.util.List;

public interface ItemService {

    ItemDtoWithBookingAndComment getItem(Long id);

    ItemDto addItem(Long userId, ItemDto item);

    List<ItemDto> getAllUsersItems(Long userId);

    ItemDto updateItem(ItemDto item, Long userId);

    List<ItemDto> searchForItems(Long userId, String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
