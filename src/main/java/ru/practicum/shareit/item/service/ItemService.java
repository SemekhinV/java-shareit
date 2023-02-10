package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {

    ItemDto getItem(Long id);

    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(ItemDto item, Long userId);
}
