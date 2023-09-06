package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping()
    public BookingAllFieldsDto addBooking(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestBody BookingFromRequestDto booking) {

        ItemDtoWithBookingAndComment item = itemService.getItem(booking.getItemId(), userId);

        return bookingService.saveBooking(booking, item, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingAllFieldsDto getBooking(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long bookingId) {

        return bookingService.getBooking(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingAllFieldsDto approveBooking(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestParam(required = false) boolean approved,
            @PathVariable Long bookingId) {

        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping()
    public List<BookingAllFieldsDto> getAllBookingsOfCurrentUser(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {

        return bookingService.getAllBookingsOfCurrentUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingAllFieldsDto> getAllUserItemsBookings(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {

        return bookingService.getAllUserItemsBookings(userId, state, from, size);
    }
}
