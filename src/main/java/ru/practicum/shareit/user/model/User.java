package ru.practicum.shareit.user.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
public class User {

    @Positive(message = "Значение поля id у user должно быть положительным.")
    Long id;

    @NotBlank(message = "Имя пользователя не указано.")
    String login;

    @Email(message = "При написании email-адреса допущена ошибка.")
    String email;
}
