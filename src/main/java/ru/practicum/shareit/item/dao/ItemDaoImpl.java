package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {

    private final HashMap<Long, Item> itemHashMap;

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(itemHashMap.values());
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.of(itemHashMap.get(id));
    }

    @Override
    public Item addItem(Item item) {

        itemHashMap.put(item.getId(), item);

        return item;
    }

    @Override
    public Item removeItem(Long id) {
        return itemHashMap.remove(id);
    }
}
