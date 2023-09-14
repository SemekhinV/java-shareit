package ru.practicum.shareit.booking.service;

import com.google.common.base.Enums;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.validation.BadInputParametersException;
import ru.practicum.shareit.exception.validation.EntityNotFoundException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
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
            throw new InvalidValueException("Начало аренды не может быть равно ее концу.");
        }

        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidValueException("Некорректно указаны временные рамки.");
        }
    }

    @Override
    @Transactional
    public BookingAllFieldsDto saveBooking(
            BookingFromRequestDto bookingDto,
            ItemDtoWithBookingAndComment itemDto,
            Long userId
    ) {

        if (itemDto.getUserId().equals(userId)) {
            throw new EntityNotFoundException("Хозяин не может арендовать собственную вещь.");
        }

        if (!itemDto.getAvailable()) {
            throw new InvalidValueException("Вещь недоступна для бронирования.");
        }

        isValid(bookingDto);

        User user = UserMapper.toUser(userService.getUser(userId));

        Item item = ItemMapper.toItem(itemDto);

        item.setOwner(user);

        List<Booking> bookings = bookingRepository.findsForIntersection(
                item.getId(),
                Status.APPROVED,
                bookingDto.getStart(),
                bookingDto.getEnd()
        );

        if (!bookings.isEmpty()) {
            throw new EntityNotFoundException("В данный промежуток времени вещь еще будет в аренде.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);

        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);

        Booking response = bookingRepository.save(booking);

        return BookingMapper.toAllFieldsDto(response);
    }

    @Override
    @Transactional
    public BookingAllFieldsDto getBooking(Long bookingId, Long userId) {

        userService.getUser(userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> {throw new EntityNotFoundException("Аренда с id = " + bookingId + " не найдена.");}
        );

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("У пользователя с id = " + userId + " нет прав для просмотра.");
        }

        return BookingMapper.toAllFieldsDto(booking);
    }


    @Override
    @Transactional
    public BookingAllFieldsDto approveBooking(Long userId, Long bookingId, boolean approve) {

        userService.getUser(userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> {throw new EntityNotFoundException("Аренда с id = " + bookingId + " не найдена.");}
        );

        if (booking.getBooker().getId().equals(userId))
            throw new EntityNotFoundException("Подтверждение возможно только хозяином вещи.");

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new InvalidValueException("У пользователя с id = " + userId + " нет прав для подтверждения.");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new InvalidValueException("Запрос уже рассмотрен");
        }

        booking.setStatus(approve ? Status.APPROVED : Status.REJECTED);

        Booking response = bookingRepository.save(booking);

        return BookingMapper.toAllFieldsDto(response);
    }

    @Override
    public List<BookingAllFieldsDto> getBookingsByItemId(Long itemId, Long userId) {

        return bookingRepository
                .findBookingsByItemIdAndItemOwnerIdIsOrderByStartDate(itemId, userId)
                .stream()
                .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                .map(BookingMapper::toAllFieldsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingAllFieldsDto> getAllBookingsOfCurrentUser(Long userId, String state) {

        userService.getUser(userId);

        List<Booking> response = null;

        Status status = (state == null) ? Status.ALL : Enums.getIfPresent(Status.class, state)
                .toJavaUtil()
                .orElseThrow(() -> {
                    throw new BadInputParametersException("Unknown state: UNSUPPORTED_STATUS");
                });

        switch (status) {

            case ALL: {

                return bookingRepository.findBookingsByBookerIdIsOrderByStartDateDesc(userId)
                        .stream()
                        .map(BookingMapper::toAllFieldsDto)
                        .collect(Collectors.toList());
            }

            case PAST: {

                response = bookingRepository.findBookingByBookerIdIsAndEndDateBeforeOrderByStartDateDesc
                        (userId, LocalDateTime.now());

                break;
            }

            case CURRENT: {

                response = bookingRepository
                        .findBookingByBookerIdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc
                                (userId, LocalDateTime.now(), LocalDateTime.now());

                break;
            }

            case FUTURE: {

                response = bookingRepository.findBookingByBookerIdIsAndStartDateAfterOrderByStartDateDesc
                        (userId, LocalDateTime.now());

                break;
            }

            default: {

                if (Arrays.asList(Status.values()).contains(status)) {

                    response = bookingRepository.findBookingByBookerIdIsAndStatusIsOrderByStartDateDesc(
                            userId, Status.valueOf(state));
                }
            }
        }

        if (response == null) {
            throw new BadInputParametersException("Unknown state: UNSUPPORTED_STATUS");
        }

        return response
                .stream()
                .map(BookingMapper::toAllFieldsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingAllFieldsDto> getAllBookingsOfCurrentUser(Long userId, String state, Pageable page) {

        userService.getUser(userId);

        List<Booking> response = null;

        Status status = (state == null) ? Status.ALL : Enums.getIfPresent(Status.class, state)
                .toJavaUtil()
                .orElseThrow(() -> {
                    throw new BadInputParametersException("Unknown state: UNSUPPORTED_STATUS");
                });

        switch (status) {

            case ALL: {

                return bookingRepository.findBookingsByBookerIdIsOrderByStartDateDesc(userId, page)
                        .stream()
                        .map(BookingMapper::toAllFieldsDto)
                        .collect(Collectors.toList());
            }

            case PAST: {

                response = bookingRepository.findBookingByBookerIdIsAndEndDateBeforeOrderByStartDateDesc
                        (userId, LocalDateTime.now(), page);

                break;
            }

            case CURRENT: {

                response = bookingRepository
                        .findBookingByBookerIdIsAndStartDateBeforeAndEndDateAfter
                                (userId, LocalDateTime.now(), LocalDateTime.now(), page);

                break;
            }

            case FUTURE: {

                response = bookingRepository.findBookingByBookerIdIsAndStartDateAfterOrderByStartDateDesc
                        (userId, LocalDateTime.now(), page);

                break;
            }

            default: {

                if (Arrays.asList(Status.values()).contains(status)) {

                    response = bookingRepository.findBookingByBookerIdIsAndStatusIsOrderByStartDateDesc(
                            userId, Status.valueOf(state), page);
                }
            }
        }

        if (response == null) {
            throw new BadInputParametersException("Unknown state: UNSUPPORTED_STATUS");
        }

        return response
                .stream()
                .map(BookingMapper::toAllFieldsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingAllFieldsDto> getAllUserItemsBookings(Long userId, String state) {

        userService.getUser(userId);

        List<Booking> response = null;

        Status status = (state == null) ? Status.ALL : Enums.getIfPresent(Status.class, state)
                .toJavaUtil()
                .orElseThrow(() -> {
                    throw new BadInputParametersException("Unknown state: UNSUPPORTED_STATUS");
                });

        switch (status) {

            case ALL: {

                return bookingRepository.findAllByItemOwnerIdIsOrderByStartDateDesc(userId)
                        .stream()
                        .map(BookingMapper::toAllFieldsDto)
                        .collect(Collectors.toList());
            }

            case PAST: {

                response = bookingRepository.findBookingByItemOwnerIdIsAndEndDateBeforeOrderByStartDateDesc(
                        userId, LocalDateTime.now());

                break;
            }

            case CURRENT: {

                response = bookingRepository.findBookingByItemOwnerIdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());

                break;
            }

            case FUTURE: {

                response = bookingRepository.findBookingByItemOwnerIdIsAndStartDateAfterOrderByStartDateDesc(
                        userId, LocalDateTime.now());

                break;
            }

            default: {

                response = bookingRepository.findBookingByItemOwnerIdIsAndStatusIsOrderByStartDateDesc(
                        userId, Status.valueOf(state));
            }
        }

        if (response == null) {
            throw new BadInputParametersException("Unknown state: UNSUPPORTED_STATUS");
        }

        return response
                .stream()
                .map(BookingMapper::toAllFieldsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingAllFieldsDto> getAllUserItemsBookings(Long userId, String state, Pageable page) {

        userService.getUser(userId);

        List<Booking> response = null;

        Status status = (state == null) ? Status.ALL : Enums.getIfPresent(Status.class, state)
                .toJavaUtil()
                .orElseThrow(() -> {
                    throw new BadInputParametersException("Unknown state: UNSUPPORTED_STATUS");
                });

        switch (status) {

            case ALL: {

                return bookingRepository.findAllByItemOwnerIdIsOrderByStartDateDesc(userId, page)
                        .stream()
                        .map(BookingMapper::toAllFieldsDto)
                        .collect(Collectors.toList());
            }

            case PAST: {

                response = bookingRepository.findBookingByItemOwnerIdIsAndEndDateBeforeOrderByStartDateDesc(
                        userId, LocalDateTime.now(), page);

                break;
            }

            case CURRENT: {

                response = bookingRepository.findBookingByItemOwnerIdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), page);

                break;
            }

            case FUTURE: {

                response = bookingRepository.findBookingByItemOwnerIdIsAndStartDateAfterOrderByStartDateDesc(
                        userId, LocalDateTime.now(), page);

                break;
            }

            default: {

                response = bookingRepository.findBookingByItemOwnerIdIsAndStatusIsOrderByStartDateDesc(
                        userId, status, page);

                break;
            }
        }

       if (response == null) {
                throw new BadInputParametersException("Unknown state: UNSUPPORTED_STATUS");
       }

        return response
                .stream()
                .map(BookingMapper::toAllFieldsDto)
                .collect(Collectors.toList());
    }
}
