package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    public static Booking toDto(BookingFromRequestDto booking) {

        return Booking.builder()
                .starDate(booking.getStart())
                .endDate(booking.getEnd())
                .build();
    }

    public static BookingAllFieldsDto toDbSave(Booking booking) {

        return BookingAllFieldsDto.builder()
                .id(booking.getId())
                .start(booking.getStarDate())
                .end(booking.getEndDate())
                .item(booking.getItem() != null ? ItemMapper.toItemDto(booking.getItem()) : null)
                .booker(booking.getUser() != null ? UserMapper.toUserDto(booking.getUser()) : null)
                .status(booking.getStatus().name())
                .build();
    }
}
