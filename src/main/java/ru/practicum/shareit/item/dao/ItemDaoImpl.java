package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {

    private final List<Item> itemList;

    @Override
    public List<Item> getAll() {
        return itemList;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.of(itemList.get(Math.toIntExact(id)));
    }

    @Override
    public Item addItem(ItemDto item, Long userId) {

        Item newItem = Item.builder()
                .id((long) itemList.size() + 1)
                .name(item.getName())
                .description(item.getDescription())
                .owner(Math.toIntExact(userId))
                .available(item.isAvailable())
                .build();

        itemList.add(newItem);

        return newItem;
    }

    @Override
    public Item removeItem(Long id) {
        return itemList.remove(Math.toIntExact(id));
    }

    @Override
    public Item updateItem(ItemDto item) {

        return itemList.set(Math.toIntExact(item.getId()),
                itemList.get(Math.toIntExact(item.getId()))
                        .toBuilder()
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.isAvailable())
                        .build()
        );

    }
}
