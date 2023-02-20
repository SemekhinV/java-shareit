package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {

    private final HashMap<Long, Item> itemHashMap;

    private Long globalId = 0L;

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(itemHashMap.values());
    }

    @Override
    public Item getItemById(Long id) {
        return itemHashMap.get(id);
    }

    @Override
    public Item addItem(Item item) {

        globalId++;

        item.setId(globalId);

        itemHashMap.put(globalId, item);

        return item;
    }

    @Override
    public Item removeItem(Long id) {
        return itemHashMap.remove(id);
    }
}
