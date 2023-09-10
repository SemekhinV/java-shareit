package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ServiceTest {

    private final ItemRequestService itemRequestService;
    private final EntityManager entityManager;
    private final UserService userService;
    private ItemRequestDto itemRequestDto;
    private UserDto user;

    @BeforeEach
    void initialize() {

        UserDto userDto = new UserDto(
                null,
                "Vas",
                "vas@mail.com"
        );

        user = userService.addUser(userDto);

        itemRequestDto = new ItemRequestDto(
                1L,
                user.getId(),
                "desc1",
                LocalDateTime.now(),
                List.of()
        );
    }

    private void addItemRequests() {

        ItemRequestDto itemRequestDto1 = new ItemRequestDto(
                1L,
                user.getId(),
                "desc2",
                LocalDateTime.now(),
                List.of()
        );

        ItemRequestDto itemRequestDto2 = new ItemRequestDto(
                2L,
                user.getId(),
                "desc3",
                LocalDateTime.now(),
                List.of()
        );

        itemRequestService.addItemRequest(itemRequestDto1, user.getId());
        itemRequestService.addItemRequest(itemRequestDto2, user.getId());
    }

    @Test
    void addItemRequestTest() {

        itemRequestService.addItemRequest(itemRequestDto, user.getId());

        ItemRequest itemRequest = entityManager
                .createQuery("SELECT itemRequest FROM ItemRequest itemRequest", ItemRequest.class)
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());

        assertThat(itemRequest.getRequester().getId(), equalTo(itemRequestDto.getUserId()));

        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void getItemRequestByIdTest() {

        itemRequestService.addItemRequest(itemRequestDto, user.getId());

        ItemRequest itemRequest = entityManager
                .createQuery("SELECT itemRequest FROM ItemRequest itemRequest", ItemRequest.class)
                .getSingleResult();

        ItemRequestDto itemRequestById = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());

        assertThat(itemRequestById.getId(), equalTo(itemRequest.getId()));

        assertThat(itemRequestById.getUserId(), equalTo(itemRequest.getRequester().getId()));

        assertThat(itemRequestById.getDescription(), equalTo(itemRequest.getDescription()));
    }

    @Test
    void getAllItemRequestsTest() {

        addItemRequests();

        List<ItemRequestDto> allItemRequests = itemRequestService.getAllItemRequests(user.getId());

        List<ItemRequest> itemRequests = entityManager
                .createQuery("SELECT itemRequest FROM ItemRequest itemRequest " +
                                "WHERE itemRequest.requester.id = :id " +
                                "ORDER BY itemRequest.created DESC ",
                        ItemRequest.class)
                .setParameter("id", user.getId())
                .getResultList();

        assertThat(allItemRequests.get(0).getId(), equalTo(itemRequests.get(0).getId()));

        assertThat(allItemRequests.size(), equalTo(itemRequests.size()));
    }

    @Test
    void getAllItemRequestsWithPageTest() {

        addItemRequests();

        List<ItemRequestDto> allItemRequests = itemRequestService.getAllItemRequests(
                user.getId(),
                null
        );

        List<ItemRequest> itemRequests = entityManager
                .createQuery("SELECT itemRequest FROM ItemRequest itemRequest " +
                                "WHERE itemRequest.requester.id <> :id " +
                                "ORDER BY itemRequest.created DESC ",
                        ItemRequest.class)
                .setParameter("id", user.getId())
                .getResultList();

        assertThat(allItemRequests.size(), equalTo(itemRequests.size()));

        assertThat(allItemRequests.size(), equalTo(0));
    }
}