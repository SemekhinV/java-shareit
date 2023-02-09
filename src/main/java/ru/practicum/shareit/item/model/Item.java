package ru.practicum.shareit.item.model;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder(toBuilder = true)
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
    int owner;

    boolean available;

    ItemRequest request;
}
