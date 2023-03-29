package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByItem_IdIsAndStatusAndEndDateAfter(Long item_id, Status status, LocalDateTime endDate);

    //ALL
    List<Booking> findBookingsByOwner_IdIsOrderByStartDate(Long owner_id);

    //PAST
    List<Booking> findBookingByOwner_IdIsAndEndDateBeforeOrderByStartDate(
            Long owner_id, LocalDateTime endDate);

    //CURRENT
    List<Booking> findBookingByOwner_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDate(
            Long owner_id, LocalDateTime startDate, LocalDateTime endDate);

    //FUTURE
    List<Booking> findBookingByOwner_IdIsAndStartDateAfterOrderByStartDate(Long owner_id, LocalDateTime startDate);

    //Для статусов WAITING/REJECTED
    List<Booking> findBookingByOwner_IdIsAndStatusIsOrderByStartDate(Long owner_id, Status status);

    //Для статусов WAITING?/REJECT, но при поиске бронирований вещей для пользователя
    List<Booking> findBookingByItem_Owner_IdIsAndStatusIsOrderByStartDate(Long owner_id, Status status);

    //PAST
    List<Booking> findBookingByItem_Owner_IdIsAndEndDateBeforeOrderByStartDate(Long owner_id, LocalDateTime endDate);

    //CURRENT
    List<Booking> findBookingByItem_Owner_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDate(
            Long owner_id, LocalDateTime starDate, LocalDateTime endDate
    );

    //FUTURE
    List<Booking> findBookingByItem_Owner_IdIsAndStartDateAfterOrderByStartDate(Long owner_id, LocalDateTime starDate);

    //Для get запроса вещи
    List<Booking> findAllByItem_IdIsAndOwner_IdIsOrderByStartDate(Long item_id, Long owner_id);
}
