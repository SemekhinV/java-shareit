package ru.practicum.shareit.item.model;


import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @Positive(message = "Значие id у вещи должно быть положительным.")
    Long id;

    @NotBlank(message = "Название вещи не может быть пустым")
    String name;

    @NotBlank(message = "Описаниеи вещи не может быть пустым")
    String description;

    @Positive
    Long owner;

    Boolean available;

    ItemRequest request;
}
