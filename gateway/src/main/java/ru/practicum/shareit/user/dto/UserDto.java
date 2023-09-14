package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.exceptions.validations.OnCreate;
import ru.practicum.shareit.exceptions.validations.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long id;

    @NotBlank(groups = OnCreate.class, message = "Name cannot be blank")
    private String name;

    @NotEmpty(groups = OnCreate.class, message = "Email не может быть пустым.")
    @Email(groups = {OnUpdate.class, OnCreate.class}, message = "Проверьте правильность ввода email.")
    private String email;
}
