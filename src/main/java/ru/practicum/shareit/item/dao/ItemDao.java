package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    List<Item> getAll();

    Item getItemById(Long id);

    Item addItem(Item item);

    Item updateItem(Item item);

    Item removeItem(Long id);
}
