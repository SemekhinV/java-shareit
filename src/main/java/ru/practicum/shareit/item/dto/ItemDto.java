package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    @Positive(message = "Значение id не может быть отрицательным.")
    Long id;

    @NotBlank(message = "Имя вещи не может быть пустым.")
    String name;

    @NotBlank(message = "Описание вещи не может быть пустым.")
    String description;

    boolean available;
}
