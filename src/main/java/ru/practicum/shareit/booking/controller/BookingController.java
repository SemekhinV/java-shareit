package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.tools.PageableImpl;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final ItemService itemService;

    private final BookingService bookingService;

    private static final String USER_ID = "X-Sharer-User-Id";

    private static final String USER_ERROR_MESSAGE = "Переадно пустое значение id пользователя";

    @PostMapping()
    public BookingAllFieldsDto addBooking(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ERROR_MESSAGE)  Long userId,
            @RequestBody BookingFromRequestDto booking
    ) {

        ItemDtoWithBookingAndComment item = itemService.getItem(booking.getItemId(), userId);

        return bookingService.saveBooking(booking, item, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingAllFieldsDto getBooking(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ERROR_MESSAGE) Long userId,
            @PathVariable Long bookingId
    ) {

        return bookingService.getBooking(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingAllFieldsDto approveBooking(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ERROR_MESSAGE) Long userId,
            @RequestParam(required = false) boolean approved,
            @PathVariable Long bookingId
    ) {

        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping()
    public List<BookingAllFieldsDto> getAllBookingsOfCurrentUser(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ERROR_MESSAGE) Long userId,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {

        return bookingService.getAllBookingsOfCurrentUser(
                userId, state, PageableImpl.of(from, size, Sort.by("startDate").descending())
        );
    }

    @GetMapping("/owner")
    public List<BookingAllFieldsDto> getAllUserItemsBookings(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = USER_ERROR_MESSAGE) Long userId,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {

        return bookingService.getAllUserItemsBookings (
                userId, state, PageableImpl.of(from, size, Sort.by("startDate").descending())
        );
    }
}
