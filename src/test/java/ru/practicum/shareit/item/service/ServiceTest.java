package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.model.Item;
import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static java.time.LocalDateTime.*;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ServiceTest {

    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void initialize() {

        userDto = userService.addUser(
                new UserDto(
                        null,
                        "Vas",
                        "vas@mail.com")
        );

        itemDto = itemService.addItem(
                userDto.getId(),
                new ItemDto(null,
                        "item1",
                        "item1 desc",
                        true,
                        userDto.getId(),
                        null),
                null
        );
    }

    private CommentDto addComments(String commentText, UserDto user) {

        UserDto booker = userService.addUser(user);

        bookingService.saveBooking(
                new BookingFromRequestDto(
                        null,
                        now().minusSeconds(2),
                        now().minusSeconds(1),
                        itemDto.getId(),
                        booker.getId(),
                        null),
                new ItemDtoWithBookingAndComment(
                        itemDto.getId(),
                        itemDto.getName(),
                        itemDto.getDescription(),
                        true,
                        userDto.getId(),
                        null,
                        null,
                        null,
                        List.of()),
                booker.getId()
        );

        CommentDto commentDto = new CommentDto(
                null,
                commentText,
                itemDto.getId(),
                booker.getName(),
                LocalDateTime.now().plusMinutes(4)
        );

        return itemService.addComment(
                commentDto,
                commentDto.getItemId(),
                booker.getId());
    }

    @Test
    void addItemTest() {

        Item item = entityManager
                .createQuery("SELECT item FROM Item item", Item.class)
                .getSingleResult();

        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));

        assertThat(item.getOwner().getId(), equalTo(itemDto.getUserId()));

        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));

        assertThat(item.getName(), equalTo(itemDto.getName()));

        assertThat(item.getId(), notNullValue());
    }

    @Test
    void getItemTest() {

        ItemDtoWithBookingAndComment itemAllFieldsDto = itemService.getItem(itemDto.getId(), itemDto.getUserId());

        Item item = entityManager
                .createQuery(
                        "SELECT item FROM Item item " +
                                "WHERE item.id = :id " +
                                "AND item.owner.id = :owner_id",
                        Item.class)
                .setParameter("owner_id", itemDto.getUserId())
                .setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(item.getDescription(), equalTo(itemAllFieldsDto.getDescription()));

        assertThat(item.getOwner().getId(), equalTo(itemAllFieldsDto.getUserId()));

        assertThat(item.getAvailable(), equalTo(itemAllFieldsDto.getAvailable()));

        assertThat(item.getName(), equalTo(itemAllFieldsDto.getName()));

        assertThat(item.getId(), notNullValue());
    }

    @Test
    void updateItemTest() {

        ItemDto dto = new ItemDto(
                itemDto.getId(),
                "Item1",
                "item1 desc",
                false,
                userDto.getId(),
                null
        );

        itemService.updateItem(dto, itemDto.getUserId());

        Item item = entityManager
                .createQuery("SELECT item FROM Item item", Item.class)
                .getSingleResult();

        assertThat(item.getDescription(), equalTo(dto.getDescription()));

        assertThat(item.getOwner().getId(), equalTo(dto.getUserId()));

        assertThat(item.getAvailable(), equalTo(dto.getAvailable()));

        assertThat(item.getName(), equalTo(dto.getName()));

        assertThat(item.getId(), notNullValue());
    }

    @Test
    void deleteItemTest() {

        itemService.deleteItem(itemDto.getId());

        List<Item> items = entityManager.createQuery(
                        "SELECT item FROM Item item " +
                                "WHERE item.id = :id " +
                                "AND item.owner.id = :ownerId",
                        Item.class)
                .setParameter("ownerId", itemDto.getUserId())
                .setParameter("id", itemDto.getId())
                .getResultList();

        assertThat(items, empty());
    }

    @Test
    void getAllUserItemsTest() {

        itemDto = itemService.addItem(
                userDto.getId(),
                new ItemDto(
                        null,
                        "item2",
                        "item2 desc",
                        true,
                        userDto.getId(),
                        null),
                null
        );

        List<ItemDto> allItems = itemService.getAllUsersItems(itemDto.getUserId(), 0, 2);

        List<Item> items = entityManager.createQuery(
                        "SELECT item FROM Item item " +
                                "WHERE item.owner.id = :ownerId",
                        Item.class)
                .setParameter("ownerId", itemDto.getUserId())
                .getResultList();

        assertThat(items.get(0).getId(), equalTo(allItems.get(0).getId()));

        assertThat(items.size(), equalTo(allItems.size()));
    }

    @Test
    void searchForItemsTest() {

        itemDto = itemService.addItem(
                userDto.getId(),
                new ItemDto(
                        1L,
                        "item3",
                        "item3 desc",
                        true,
                        userDto.getId(),
                        null),
                null
        );

        List<ItemDto> itemDtoList = itemService.searchForItems(itemDto.getUserId(), "desc", 0, 2);

        List<Item> items = entityManager.createQuery(
                        "SELECT item FROM Item item " +
                                "WHERE item.available = TRUE " +
                                "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
                                "OR UPPER(item.description) LIKE UPPER(CONCAT('%', :text, '%')))",
                        Item.class)
                .setParameter("text", "desc")
                .getResultList();

        assertThat(items.get(0).getId(), equalTo(itemDtoList.get(0).getId()));

        assertThat(items.size(), equalTo(itemDtoList.size()));
    }

    @Test
    void addCommentTest() {

        CommentDto commentDto = addComments(
                "comment1",
                new UserDto(
                        15L,
                        "Den",
                        "den@mail.com")
        );

        Comment comment = entityManager.createQuery(
                "SELECT comment FROM Comment comment",
                Comment.class).getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getId(), equalTo(commentDto.getId()));

        assertThat(comment.getItem().getId(), equalTo(commentDto.getItemId()));

        assertThat(comment.getAuthor().getName(), equalTo(commentDto.getAuthorName()));

        assertThat(comment.getText(), equalTo(commentDto.getText()));
    }

    @Test
    void getAllCommentsTest() {

         addComments(
                "com1",
                new UserDto(
                        12L,
                        "Vas",
                        "vas2@mail.com")
        );

        addComments(
                "com2",
                new UserDto(
                        13L,
                        "Ivan",
                        "ivan@mail.com")
        );

        List<CommentDto> allComments = itemService.getAllComments();

        List<Comment> comments = entityManager.createQuery(
                        "SELECT comment FROM Comment comment",
                        Comment.class)
                .getResultList();

        assertThat(comments.size(), equalTo(allComments.size()));

        assertThat(comments.size(), equalTo(2));

        assertThat(comments, notNullValue());
    }

    @Test
    void getItemsByRequestIdTest() {

        UserDto requester = userService.addUser(
                new UserDto(
                        null,
                        "Vas",
                        "vas3@mail.com")
        );

        ItemRequestDto itemRequestDto = itemRequestService.addItemRequest(
                new ItemRequestDto(
                        null,
                        requester.getId(),
                        "test request",
                        LocalDateTime.now(),
                        List.of()),
                requester.getId()
        );

        itemService.addItem(
                userDto.getId(),
                new ItemDto(
                        null,
                        "item4",
                        "item4 desc",
                        true,
                        userDto.getId(),
                        null),
                itemRequestDto
        );

        List<ItemDto> itemsByRequestId = itemService.getItemsByRequestId(itemRequestDto.getId());

        List<Item> itemsByRequest = entityManager.createQuery(
                        "SELECT item FROM Item item " +
                                "WHERE item.request.id = :requestId",
                        Item.class)
                .setParameter("requestId", itemRequestDto.getId())
                .getResultList();

        assertThat(itemsByRequestId.size(), equalTo(itemsByRequest.size()));

        assertThat(itemsByRequestId.size(), equalTo(1));

        assertThat(itemsByRequestId, notNullValue());
    }
}
