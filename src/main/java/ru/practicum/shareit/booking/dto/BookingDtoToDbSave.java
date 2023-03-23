package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class BookingDtoToDbSave {

    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    Long bookerId;
    String status;
}
