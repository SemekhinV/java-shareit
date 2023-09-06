package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemService {

    @Transactional
    List<ItemDto> getAllUsersItems(Long userId, Integer from, Integer size);

    ItemDtoWithBookingAndComment getItem(Long id, Long userId);

    ItemDto addItem(Long userId, ItemDto item, ItemRequestDto itemRequestDto);

    ItemDto updateItem(ItemDto item, Long userId);

    void deleteItem(Long itemId);

    @Transactional
    List<ItemDto> searchForItems(Long userId, String text, Integer from, Integer size);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);

    List<CommentDto> getAllItemComments(Long id);

    List<CommentDto> getAllComments();

    List<ItemDto> getItemsByRequestId(Long requestId);

    @Transactional
    List<ItemDto> findItemsByRequestsList(List<ItemRequest> requests);
}
