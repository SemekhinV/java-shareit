package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemService itemService;

    private final UserService userService;

    public BookingAllFieldsDto addBooking(BookingFromRequestDto booking, ItemDtoWithBookingAndComment item, Long userId) {

        return null;
    }

    public BookingAllFieldsDto updateBooking(BookingFromRequestDto booking, ItemDtoWithBookingAndComment item, Long userId) {

        return null;
    }

    public BookingAllFieldsDto getBooking(Long bookingId, Long userId) {

        return null;
    }

    
}
