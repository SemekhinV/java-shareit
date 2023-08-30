package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .available(item.getAvailable())
                .build();
    }

    public static ItemDtoWithBookingAndComment mapToItemDtoWithBookingAndComment(Item item,
                                                         BookingAllFieldsDto lastBooking,
                                                         BookingAllFieldsDto futureBooking,
                                                         List<CommentDto> comments) {
        return new ItemDtoWithBookingAndComment(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking != null ? new BookingDto(lastBooking.getId(), lastBooking.getBooker().getId()) : null,
                futureBooking != null ? new BookingDto(futureBooking.getId(), futureBooking.getBooker().getId()) : null,
                comments != null ? comments : List.of()
        );
    }

    public static Item toItem(ItemDto item) {

        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDtoWithBookingAndComment item) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

}

