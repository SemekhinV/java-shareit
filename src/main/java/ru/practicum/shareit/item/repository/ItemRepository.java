package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdIs(Long ownerId, Pageable pageable);

    List<Item> findAllByRequestIdIs(Long requestId);

    List<Item> findAllByRequestIn(List<ItemRequest> requests);

    @Query(
            "SELECT item FROM Item item " +
            "WHERE item.available = true " +
            "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            "OR UPPER(item.description) LIKE UPPER(CONCAT('%', ?1, '%')))"
    )
    List<Item> searchForItems(String search);

    @Query(
            "SELECT item FROM Item item " +
                    "WHERE item.available = true " +
                    "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
                    "OR UPPER(item.description) LIKE UPPER(CONCAT('%', ?1, '%')))"
    )
    List<Item> searchForItems(String search, Pageable pageable);
}
