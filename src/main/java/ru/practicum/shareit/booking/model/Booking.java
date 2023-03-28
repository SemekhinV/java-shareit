package ru.practicum.shareit.booking.model;


import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Booking")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User owner;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    Item item;

    @Column(name = "start_date", nullable = false)
    LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    Status status;
}
