package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.exceptions.validations.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Long id;

    @NotBlank(groups = OnCreate.class, message = "Название вещи не может быть пустым.")
    String name;

    @NotBlank(groups = OnCreate.class, message = "Описание вещи не может быть пустым.")
    String description;

    @NotNull(groups = OnCreate.class, message = "Доступность вещи не может быть пустой.")
    Boolean available;

    Long userId;

    Long requestId;
}
