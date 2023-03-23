package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Getter @Setter @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "Значение поля id у user должно быть положительным.")
    Long id;

    @Column(nullable = false)
    @NotBlank(message = "Имя пользователя не указано.")
    String name;

    @Column(nullable = false, unique = true)
    String email;
}
