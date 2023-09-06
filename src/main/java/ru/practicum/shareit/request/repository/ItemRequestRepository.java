package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import ru.practicum.shareit.request.model.ItemRequest;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findItemRequestsByRequester_IdIsOrderByCreatedDesc(Long requester_id);

    List<ItemRequest> findItemRequestsByRequester_IdIsOrderByCreatedDesc(Long requester_id, Pageable pageable);
}
