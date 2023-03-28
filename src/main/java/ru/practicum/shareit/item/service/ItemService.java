package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;

import java.util.List;

public interface ItemService {

    ItemDtoWithBookingAndComment getItem(Long id, Long userId);

    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(ItemDto item, Long userId);

    void deleteItem(Long itemId);

    List<ItemDto> getAllUsersItems(Long userId);

    List<ItemDto> searchForItems(Long userId, String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);

    List<CommentDto> getAllItemComments(Long id);

    List<CommentDto> getAllComments();

    List<ItemDto> getItemsByRequestId(Long requestId);
}
