package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner_IdIs(Long owner_id);

    List<Item> findAllByRequest_IdIs(Long request_id);

    @Query(
            "SELECT item FROM Item item " +
            "WHERE item.available = true " +
            "AND (UPPER(item.name) LIKE UPPER(:search)) " +
            "OR (UPPER(item.description) LIKE UPPER(:search))"
    )
    List<Item> searchForItems(String search);
}
