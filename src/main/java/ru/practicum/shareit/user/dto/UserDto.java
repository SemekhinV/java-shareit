package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    @NonNull
    Long userId;

    @NotBlank(message = "Необходимо указать login пользователя.")
    String userLogin;

    @Email(message = "Необходимо указать email пользователя.")
    String userEmail;
}
