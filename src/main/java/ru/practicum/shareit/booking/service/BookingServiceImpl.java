package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.validation.*;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;

    private void isValid(BookingFromRequestDto booking) {

        if (booking.getStart() == null) {
            throw new InvalidValueException("Дата старта аренды не может быть пустой.");
        }
        if (booking.getEnd() == null) {
            throw new InvalidValueException("Дата конца аренды не может быть пустой.");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidValueException("Дата старта аренды не может быть в прошлом.");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new InvalidValueException("Даты старта и конца аренды не могут совпадать.");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidValueException("Некорректно указаны временные рамки.");
        }
    }

    private void isIdValid(Long id1, Long id2) {

        if (id1 == null || id2 == null) {
            throw new BadInputParametersException("Передан пустой идентификатор.");
        }
    }

    @Override
    @Transactional
    public BookingAllFieldsDto saveBooking(BookingFromRequestDto bookingDto, ItemDtoWithBookingAndComment itemDto, Long userId) {

        if (userId == null) {throw new BadInputParametersException("Передано пустое id пользователя.");}

        if (!itemDto.getAvailable()) {
            throw new ItemUnavailableException("Вещь недоступна для бронирования.");
        }

        isValid(bookingDto);

        User user = UserMapper.toUser(userService.getUser(userId));

        Item item = ItemMapper.toItem(itemDto);

        if (!bookingRepository.findBookingsByItem_IdIsAndStatusAndEndDateAfter(
                itemDto.getId(),
                Status.APPROVED,
                bookingDto.getEnd()
        ).isEmpty()) {
            throw new ItemIsAlreadyBookingException("В данный промежуток времени вещь еще будет в аренде.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);

        booking.setItem(item);
        booking.setOwner(user);
        booking.setStatus(Status.WAITING);

        Booking response = bookingRepository.save(booking);

        return BookingMapper.toAllFieldsDto(response);
    }

    @Override
    public BookingAllFieldsDto getBooking(Long bookingId, Long userId) {

        isIdValid(bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> {throw new EntityNotFoundException("Аренда с id = " + bookingId + " не найдена.");}
        );

        if (!booking.getOwner().getId().equals(userId) || !booking.getItem().getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("У пользователя с id = " + userId + " нет прав для просмотра.");
        }

        return BookingMapper.toAllFieldsDto(booking);
    }

    @Override
    public List<BookingAllFieldsDto> getBookingsByItemId(Long itemId, Long userId) {

        return bookingRepository
                .findAllByItem_IdIsAndOwner_IdIsOrderByStartDate(itemId, userId)
                .stream()
                .map(BookingMapper::toAllFieldsDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingAllFieldsDto approveBooking(Long userId, Long bookingId, boolean approve) {

        isIdValid(userId, bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> {throw new EntityNotFoundException("Аренда с id = " + bookingId + " не найдена.");}
        );

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("У пользователя с id = " + userId + " нет прав для подтверждения.");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new PermissionDeniedException("Запрос на аренду уже был рассмотрен.");
        }

        booking.setStatus(approve ? Status.APPROVED : Status.REJECTED);

        Booking response = bookingRepository.save(booking);

        return BookingMapper.toAllFieldsDto(response);
    }

    @Override
    public List<BookingAllFieldsDto> getAllBookingsOfCurrentUser(Long userId, String state) {

        if (userId == null) {throw new BadInputParametersException("Передан пустой параметр.");}

        List<Booking> response = null;

        if (Status.ALL.name().equals(state) || state == null) {
            response = bookingRepository.findBookingsByOwner_IdIsOrderByStartDate(userId);
        }

        if (Status.PAST.name().equals(state)) {
            response = bookingRepository.findBookingByOwner_IdIsAndEndDateBeforeOrderByStartDate
                    (userId, LocalDateTime.now());
        }

        if (Status.CURRENT.name().equals(state)) {
            response = bookingRepository
                    .findBookingByOwner_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDate
                            (userId, LocalDateTime.now(), LocalDateTime.now());
        }

        if (Status.CURRENT.name().equals(state)) {
            response = bookingRepository.findBookingByOwner_IdIsAndStartDateAfterOrderByStartDate
                    (userId, LocalDateTime.now());
        }

        if (Arrays.asList(Status.values()).contains(Status.valueOf(state))) {

            response = bookingRepository.findBookingByOwner_IdIsAndStatusIsOrderByStartDate(userId, Status.valueOf(state));
        }

        if (response == null) {
            throw new EntityNotFoundException("Не удалось найти бронирование по выбранным критериям.");
        }

        return response
                .stream()
                .map(BookingMapper::toAllFieldsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingAllFieldsDto> getAllUserItemsBooking(Long userId, String state) {

        if (userId == null) {throw new BadInputParametersException("Передано пустое значение.");}

        List<Booking> response = null;

        if (Status.ALL.name().equals(state) || state == null) {

            return bookingRepository.findBookingsByOwner_IdIsOrderByStartDate(userId)
                    .stream()
                    .map(BookingMapper::toAllFieldsDto)
                    .collect(Collectors.toList());
        }

        if (Status.PAST.name().equals(state)) {

            response = bookingRepository.findBookingByItem_Owner_IdIsAndEndDateBeforeOrderByStartDate(
                    userId, LocalDateTime.now()
            );
        }

        if (Status.CURRENT.name().equals(state)) {

            response = bookingRepository.findBookingByItem_Owner_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDate(
                    userId, LocalDateTime.now(), LocalDateTime.now()
            );
        }

        if (Status.FUTURE.name().equals(state)) {

            response = bookingRepository.findBookingByItem_Owner_IdIsAndStartDateAfterOrderByStartDate(
                    userId, LocalDateTime.now()
            );
        }

        if (Arrays.asList(Status.values()).contains(Status.valueOf(state))) {

            response = bookingRepository.findBookingByItem_Owner_IdIsAndStatusIsOrderByStartDate(
                    userId, Status.valueOf(state)
            );
        }

        if (response == null) {
            throw new EntityNotFoundException("Не удалось найти бронирование по выбранным критериям.");
        }

        return response
                .stream()
                .map(BookingMapper::toAllFieldsDto)
                .collect(Collectors.toList());
    }
}
