package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking toBooking(BookingFromRequestDto booking) {

        return Booking.builder()
                .startDate(booking.getStart())
                .endDate(booking.getEnd())
                .build();
    }

    public static BookingAllFieldsDto toAllFieldsDto(Booking booking) {

        return BookingAllFieldsDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .item(booking.getItem() != null ? ItemMapper.toItemDto(booking.getItem()) : null)
                .booker(booking.getBooker() != null ? UserMapper.toUserDto(booking.getBooker()) : null)
                .status(booking.getStatus().name())
                .build();
    }
}
