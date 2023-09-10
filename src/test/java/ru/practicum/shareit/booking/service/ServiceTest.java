package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.shareit.booking.status.Status.APPROVED;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ServiceTest {

    private BookingAllFieldsDto bookingAllFieldsDto;
    private final BookingService bookingService;
    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemService itemService;
    private ItemDto itemDto;
    private UserDto owner;

    @BeforeEach
    void initialize() {

        owner = userService.addUser(
                new UserDto(
                        null,
                        "Vas",
                        "vas@mail.com")
        );

        UserDto booker = userService.addUser(
                new UserDto(
                        null,
                        "Vas2",
                        "vas2@mail.com")
        );

        itemDto = itemService.addItem(
                owner.getId(),
                new ItemDto(
                        null,
                        "item1",
                        "item1 desc",
                        true,
                        owner.getId(),
                        null),
                null
        );
        ItemDtoWithBookingAndComment itemDtoWithBookingAndComment = new ItemDtoWithBookingAndComment(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                true,
                owner.getId(),
                null,
                null,
                null,
                List.of()
        );

        BookingFromRequestDto bookingFromRequestDto = BookingFromRequestDto
                .builder()
                .id(1L)
                .start(now().plusSeconds(1))
                .end(now().plusMinutes(40))
                .itemId(1L)
                .bookerId(1L)
                .status(APPROVED.name())
                .build();

        bookingAllFieldsDto = bookingService.saveBooking(
                bookingFromRequestDto,
                itemDtoWithBookingAndComment,
                booker.getId()
        );
    }

    @Test
    void saveBookingTest() {

        Booking booking = entityManager
                .createQuery("SELECT booking FROM Booking booking", Booking.class)
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker().getId(), equalTo(bookingAllFieldsDto.getBooker().getId()));
        assertThat(booking.getItem().getId(), equalTo(bookingAllFieldsDto.getItem().getId()));
    }

    @Test
    void getBookingTest() {

        BookingAllFieldsDto approved = bookingService.getBooking(
                bookingAllFieldsDto.getId(),
                bookingAllFieldsDto.getBooker().getId()
        );

        Booking booking = entityManager
                .createQuery(
                        "SELECT booking FROM Booking booking " +
                                "WHERE booking.id = :id AND booking.booker.id = :bookerId",
                        Booking.class)
                .setParameter("bookerId", bookingAllFieldsDto.getBooker().getId())
                .setParameter("id", bookingAllFieldsDto.getId())
                .getSingleResult();

        assertThat(approved.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(approved.getStart(), equalTo(booking.getStartDate()));
        assertThat(approved.getId(), equalTo(booking.getId()));
    }

    @Test
    void getBookingsByItemIdTest() {

        List<BookingAllFieldsDto> bookingsFrom = bookingService.getBookingsByItemId(
                itemDto.getId(),
                owner.getId()
        );

        List<Booking> bookings = entityManager.createQuery(
                        "SELECT booking FROM Booking booking " +
                                "JOIN booking.item item " +
                                "WHERE item.owner.id = :ownerId AND item.id = :itemId",
                        Booking.class)
                .setParameter("ownerId", owner.getId())
                .setParameter("itemId", itemDto.getId())
                .getResultList();

        assertThat(bookingsFrom.get(0).getId(), equalTo(bookings.get(0).getId()));
        assertThat(bookingsFrom.size(), equalTo(bookings.size()));
    }

    @Test
    void getAllUserItemsBookingsTest() {

        var bookings = bookingService.getAllUserItemsBookings(
                owner.getId(),
                null,
                null
        );

        var booking = entityManager.createQuery(
                        "SELECT booking FROM Booking booking " +
                                "JOIN booking.item item " +
                                "WHERE item.owner.id = :id",
                        Booking.class)
                .setParameter("id", owner.getId())
                .getResultList();

        assertThat(bookings.get(0).getId(), equalTo(booking.get(0).getId()));
        assertThat(bookings.size(), equalTo(booking.size()));
    }

    @Test
    void getAllUserItemsBookingsWithStatusTest() {

        List<BookingAllFieldsDto> bookings = bookingService.getAllUserItemsBookings(
                owner.getId(),
                APPROVED.name(),
                null
        );

        List<Booking> approvedBookings = entityManager.createQuery(
                        "SELECT booking FROM Booking booking " +
                                "JOIN booking.item item " +
                                "WHERE item.owner.id = :id AND booking.status = :status",
                        Booking.class)
                .setParameter("id", owner.getId())
                .setParameter("status", APPROVED)
                .getResultList();

        assertThat(bookings.size(), equalTo(approvedBookings.size()));
        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void getAllBookingsOfCurrentUserTest() {

        List<BookingAllFieldsDto> approved = bookingService.getAllBookingsOfCurrentUser(
                bookingAllFieldsDto.getBooker().getId(),
                null,
                null);

        List<Booking> booking = entityManager.createQuery(
                        "SELECT booking FROM Booking booking " +
                                "WHERE booking.booker.id = :id",
                        Booking.class)
                .setParameter("id", bookingAllFieldsDto.getBooker().getId())
                .getResultList();

        assertThat(approved.get(0).getId(), equalTo(booking.get(0).getId()));
        assertThat(approved.size(), equalTo(booking.size()));
    }

    @Test
    void getAllBookingsOfCurrentUserEmptyListTest() {

        var allBookings = bookingService.getAllBookingsOfCurrentUser(
                bookingAllFieldsDto.getBooker().getId(),
                APPROVED.name(),
                null
        );

        var approved = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "WHERE booking.booker.id = :id AND booking.status = :status",
                        Booking.class)
                .setParameter("id", bookingAllFieldsDto.getBooker().getId())
                .setParameter("status", APPROVED)
                .getResultList();

        assertThat(allBookings.size(), equalTo(approved.size()));
        assertThat(allBookings.size(), equalTo(0));
    }
}
