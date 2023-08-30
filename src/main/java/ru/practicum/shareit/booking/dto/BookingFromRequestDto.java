package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class BookingFromRequestDto {

    Long id;
    @NonNull
    LocalDateTime start;

    @NonNull
    LocalDateTime end;

    @NonNull
    Long itemId;

    @NonNull
    Long bookerId;

    @NonNull
    String status;
}
