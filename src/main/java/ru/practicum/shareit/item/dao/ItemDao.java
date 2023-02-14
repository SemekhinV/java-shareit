package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {

    List<Item> getAll();

    Optional<Item> getItemById(Long id);

    Item addItem(Item item);

    Item removeItem(Long id);
}
