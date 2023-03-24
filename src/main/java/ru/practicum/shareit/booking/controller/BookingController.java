package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.service.ItemService;

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

        return bookingService.addBooking(booking, item, userId);
    }
}
