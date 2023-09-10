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
            Long itemId, Status status, LocalDateTime startDate, LocalDateTime endDate);

    //ALL
    List<Booking> findBookingsByBookerIdIsOrderByStartDateDesc(Long bookerId);

    List<Booking> findBookingsByBookerIdIsOrderByStartDateDesc(Long bookerId, Pageable pageable);

    //PAST
    List<Booking> findBookingByBookerIdIsAndEndDateBeforeOrderByStartDateDesc(
            Long bookerId, LocalDateTime endDate);

    List<Booking> findBookingByBookerIdIsAndEndDateBeforeOrderByStartDateDesc(
            Long bookerId, LocalDateTime endDate, Pageable pageable);

    //CURRENT
    List<Booking> findBookingByBookerIdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findBookingByBookerIdIsAndStartDateBeforeAndEndDateAfter(
            Long bookerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    //FUTURE
    List<Booking> findBookingByBookerIdIsAndStartDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime startDate);

    List<Booking> findBookingByBookerIdIsAndStartDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime startDate, Pageable pageable);

    //Для статусов WAITING/REJECTED
    List<Booking> findBookingByBookerIdIsAndStatusIsOrderByStartDateDesc(Long bookerId, Status status);

    List<Booking> findBookingByBookerIdIsAndStatusIsOrderByStartDateDesc(
            Long bookerId, Status status, Pageable pageable);

    //Для статусов WAITING/REJECT, но при поиске бронирований вещей для пользователя
    List<Booking> findBookingByItemOwnerIdIsAndStatusIsOrderByStartDateDesc(Long ownerId, Status status);

    List<Booking> findBookingByItemOwnerIdIsAndStatusIsOrderByStartDateDesc(
            Long ownerId, Status status, Pageable pageable);

    //PAST
    List<Booking> findBookingByItemOwnerIdIsAndEndDateBeforeOrderByStartDateDesc(Long ownerId, LocalDateTime endDate);

    List<Booking> findBookingByItemOwnerIdIsAndEndDateBeforeOrderByStartDateDesc(
            Long ownerId, LocalDateTime endDate, Pageable pageable);

    //CURRENT
    List<Booking> findBookingByItemOwnerIdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long ownerId, LocalDateTime starDate, LocalDateTime endDate
    );

    List<Booking> findBookingByItemOwnerIdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long ownerId, LocalDateTime starDate, LocalDateTime endDate, Pageable pageable
    );

    //FUTURE
    List<Booking> findBookingByItemOwnerIdIsAndStartDateAfterOrderByStartDateDesc(Long ownerId, LocalDateTime starDate);

    List<Booking> findBookingByItemOwnerIdIsAndStartDateAfterOrderByStartDateDesc(
            Long ownerId, LocalDateTime starDate, Pageable pageable);

    //Для get запроса вещи
    List<Booking> findBookingsByItemIdAndItemOwnerIdIsOrderByStartDate(Long itemId, Long ownerId);

    //Для All запроса в методе с owner
    List<Booking> findAllByItemOwnerIdIsOrderByStartDateDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdIsOrderByStartDateDesc(Long ownerId, Pageable pageable);
}
