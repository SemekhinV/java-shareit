package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;

public interface BookingService {
    BookingAllFieldsDto addBooking(BookingFromRequestDto booking, ItemDtoWithBookingAndComment item, Long userId);
}
