package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithBookingAndComment extends ItemDto {

    BookingDto lastBooking;

    BookingDto nextBooking;

    List<CommentDto> comments;

    public ItemDtoWithBookingAndComment(Long id,
                                        String name,
                                        String description,
                                        Boolean available,
                                        Long userId,
                                        Long requestId,
                                        BookingDto lastBooking,
                                        BookingDto nextBooking,
                                        List<CommentDto> comments) {

        super(id, name, description, available, userId, requestId);

        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
