package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItem(Long id);

    ItemDto addItem(Long userId, ItemDto item);

    List<ItemDto> getAllUsersItems(Long userId);

    ItemDto updateItem(ItemDto item, Long userId);

    List<ItemDto> searchForItems(Long userId, String text);
}
