package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //findBookingsByItem_IdIsAndStatusIsAndEndDateAfter


    //Проверка на вхождение даты 1. концом в промежуток 2. началом в промежуток 3. целиком в промежуток
    //Или начало переданной аренды начинается до и заканчивается после промежутка
    @Query(
            "SELECT b from Booking b " +
                    "WHERE b.item.id = ?1 " +
                    "AND b.status = ?2 " +
                    "AND ((?3 BETWEEN b.startDate AND b.endDate) " +
                    "OR (?4 BETWEEN b.startDate AND b.endDate)" +
                    "OR (b.startDate BETWEEN ?3 AND ?4))"
    )
    List<Booking> findsForIntersection(
            Long item_id, Status status, LocalDateTime startDate, LocalDateTime endDate);

    //ALL
    List<Booking> findBookingsByBooker_IdIsOrderByStartDateDesc(Long booker_id);
    List<Booking> findBookingsByBooker_IdIsOrderByStartDateDesc(Long booker_id, Pageable pageable);

    //PAST
    List<Booking> findBookingByBooker_IdIsAndEndDateBeforeOrderByStartDateDesc(
            Long booker_id, LocalDateTime endDate);
    List<Booking> findBookingByBooker_IdIsAndEndDateBeforeOrderByStartDateDesc(
            Long booker_id, LocalDateTime endDate, Pageable pageable);

    //CURRENT
    List<Booking> findBookingByBooker_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long booker_id, LocalDateTime startDate, LocalDateTime endDate);
    List<Booking> findBookingByBooker_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long booker_id, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    //FUTURE
    List<Booking> findBookingByBooker_IdIsAndStartDateAfterOrderByStartDateDesc(
            Long booker_id, LocalDateTime startDate);
    List<Booking> findBookingByBooker_IdIsAndStartDateAfterOrderByStartDateDesc(
            Long booker_id, LocalDateTime startDate, Pageable pageable);

    //Для статусов WAITING/REJECTED
    List<Booking> findBookingByBooker_IdIsAndStatusIsOrderByStartDateDesc(Long booker_id, Status status);

    //Для статусов WAITING/REJECT, но при поиске бронирований вещей для пользователя
    List<Booking> findBookingByItem_Owner_IdIsAndStatusIsOrderByStartDateDesc(Long owner_id, Status status);

    //PAST
    List<Booking> findBookingByItem_Owner_IdIsAndEndDateBeforeOrderByStartDateDesc(Long owner_id, LocalDateTime endDate);

    //CURRENT
    List<Booking> findBookingByItem_Owner_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long owner_id, LocalDateTime starDate, LocalDateTime endDate
    );

    //FUTURE
    List<Booking> findBookingByItem_Owner_IdIsAndStartDateAfterOrderByStartDateDesc(Long owner_id, LocalDateTime starDate);

    //Для get запроса вещи
    @Query(
            "select b from Booking b " +
                    "where b.item.id = ?1 " +
                    "and b.item.owner.id = ?2 " +
                    "and b.status = 'APPROVED' " +
                    "order by b.startDate"
    )
    List<Booking> findBookingsForItemGerRequest(Long item_id, Long owner_id);

    //Для All запроса в методе с owner
    List<Booking> findAllByItem_Owner_IdIsOrderByStartDateDesc(Long owner_id);
    List<Booking> findAllByItem_Owner_IdIsOrderByStartDateDesc(Long owner_id, Pageable pageable);
}
