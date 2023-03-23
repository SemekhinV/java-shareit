package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Items")
@Getter @Setter @ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "Значие id у вещи должно быть положительным.")
    Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Название вещи не может быть пустым")
    String name;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Описаниеи вещи не может быть пустым")
    String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    User owner;

    @Column(name = "is_available", nullable = false)
    Boolean available;

    @ManyToOne
    @JoinColumn(name = "request_id")
    ItemRequest request;
}
