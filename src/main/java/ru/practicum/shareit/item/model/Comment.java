package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@RequiredArgsConstructor
@Getter @Setter @ToString
@Table(name = "Comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    Item item;

    @Column(name = "text", nullable = false)
    String text;

    @Column(nullable = false)
    LocalDateTime created;
}
