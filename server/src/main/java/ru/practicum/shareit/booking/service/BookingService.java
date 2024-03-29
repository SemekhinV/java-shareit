package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;

import java.util.List;

public interface BookingService {
    BookingAllFieldsDto saveBooking(BookingFromRequestDto booking, ItemDtoWithBookingAndComment item, Long userId);

    BookingAllFieldsDto getBooking(Long bookingId, Long userId);

    List<BookingAllFieldsDto> getBookingsByItemId(Long itemId, Long userId);

    BookingAllFieldsDto approveBooking(Long userId, Long bookingId, boolean approve);

    List<BookingAllFieldsDto> getAllBookingsOfCurrentUser(Long userId, String state);

    List<BookingAllFieldsDto> getAllBookingsOfCurrentUser(Long userId, String state, Pageable pageable);

    List<BookingAllFieldsDto> getAllUserItemsBookings(Long userId, String state);

    List<BookingAllFieldsDto> getAllUserItemsBookings(Long userId, String state, Pageable pageable);
}
