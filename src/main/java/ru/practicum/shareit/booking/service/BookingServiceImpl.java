package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dtotest.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.validation.*;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.tools.PageRequestImpl;
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

        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new InvalidValueException("Передано пустое значения даты.");
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

    private void isIdValid(Long id1, Long id2) {

        if (id1 == null || id2 == null) {
            throw new BadInputParametersException("Передан пустой идентификатор.");
        }
    }

    @Override
    @Transactional
    public BookingAllFieldsDto saveBooking(
            BookingFromRequestDto bookingDto,
            ItemDtoWithBookingAndComment itemDto,
            Long userId
    ) {

        if (userId == null) {throw new BadInputParametersException("Передано пустое id пользователя.");}

        if (itemDto.getUserId().equals(userId)) {
            throw new EntityNotFoundException("Хозяин не может арендовать собственную вещь.");
        }

        if (!itemDto.getAvailable()) {
            throw new InvalidValueException("Вещь недоступна для бронирования.");
        }

        isValid(bookingDto);

        User user = UserMapper.toUser(userService.getUser(userId));

        Item item = ItemMapper.toItem(itemDto);

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

        isIdValid(bookingId, userId);

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

        isIdValid(userId, bookingId);

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
                .findBookingsByItem_IdAndItem_Owner_IdIsOrderByStartDate(itemId, userId)
                .stream()
                .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                .map(BookingMapper::toAllFieldsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingAllFieldsDto> getAllBookingsOfCurrentUser(Long userId, String state) {

        if (userId == null) {throw new BadInputParametersException("Передан пустой параметр.");}

        userService.getUser(userId);

        List<Booking> response = null;

        if (Status.ALL.name().equals(state) || state == null) {

            return bookingRepository.findBookingsByBooker_IdIsOrderByStartDateDesc(userId)
                    .stream()
                    .map(BookingMapper::toAllFieldsDto)
                    .collect(Collectors.toList());
        }

        if (Status.PAST.name().equals(state)) {

            response = bookingRepository.findBookingByBooker_IdIsAndEndDateBeforeOrderByStartDateDesc
                        (userId, LocalDateTime.now());
        }

        if (Status.CURRENT.name().equals(state)) {

            response = bookingRepository
                        .findBookingByBooker_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc
                                (userId, LocalDateTime.now(), LocalDateTime.now());
        }

        if (Status.FUTURE.name().equals(state)) {

            response = bookingRepository.findBookingByBooker_IdIsAndStartDateAfterOrderByStartDateDesc
                        (userId, LocalDateTime.now());
        }

        //Использовать метод .contains() не получилось, тк нет возможности нормально обработать ситуацию с
        //Неизвестным программе статусом
        if (response == null && Arrays.stream(Status.values()).anyMatch(status -> status.name().equals(state))) {

            response = bookingRepository.findBookingByBooker_IdIsAndStatusIsOrderByStartDateDesc(
                        userId, Status.valueOf(state));
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
    public List<BookingAllFieldsDto> getAllBookingsOfCurrentUser(Long userId, String state, Integer from, Integer size) {

        if (userId == null) {throw new BadInputParametersException("Передан пустой параметр.");}

        userService.getUser(userId);

        var page = PageRequestImpl.of(from, size, Sort.by("startDate").descending());

        List<Booking> response = null;

        if (Status.ALL.name().equals(state) || state == null) {

            if (page != null) {

                return bookingRepository.findBookingsByBooker_IdIsOrderByStartDateDesc(userId, page)
                        .stream()
                        .map(BookingMapper::toAllFieldsDto)
                        .collect(Collectors.toList());

            } else {

                return bookingRepository.findBookingsByBooker_IdIsOrderByStartDateDesc(userId)
                        .stream()
                        .map(BookingMapper::toAllFieldsDto)
                        .collect(Collectors.toList());
            }

        }

        if (Status.PAST.name().equals(state)) {

            if (page != null) {

                response = bookingRepository.findBookingByBooker_IdIsAndEndDateBeforeOrderByStartDateDesc
                        (userId, LocalDateTime.now(), page);
            } else {

                response = bookingRepository.findBookingByBooker_IdIsAndEndDateBeforeOrderByStartDateDesc
                        (userId, LocalDateTime.now());
            }
        }

        if (Status.CURRENT.name().equals(state)) {

            if (page != null) {

                response = bookingRepository
                        .findBookingByBooker_IdIsAndStartDateBeforeAndEndDateAfter
                                (userId, LocalDateTime.now(), LocalDateTime.now(), page);
            } else {

                response = bookingRepository
                        .findBookingByBooker_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc
                                (userId, LocalDateTime.now(), LocalDateTime.now());
            }
        }

        if (Status.FUTURE.name().equals(state)) {

            if (page != null) {

                response = bookingRepository.findBookingByBooker_IdIsAndStartDateAfterOrderByStartDateDesc
                        (userId, LocalDateTime.now(), page);
            } else {

                response = bookingRepository.findBookingByBooker_IdIsAndStartDateAfterOrderByStartDateDesc
                        (userId, LocalDateTime.now());
            }
        }

        //Использовать метод .contains() не получилось, тк нет возможности нормально обработать ситуацию с
        //Неизвестным программе статусом
        if (response == null && Arrays.stream(Status.values()).anyMatch(status -> status.name().equals(state))) {

            if (page != null) {

                response = bookingRepository.findBookingByBooker_IdIsAndStatusIsOrderByStartDateDesc(
                        userId, Status.valueOf(state), page
                );
            } else {

                response = bookingRepository.findBookingByBooker_IdIsAndStatusIsOrderByStartDateDesc(
                        userId, Status.valueOf(state)
                );
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

        if (userId == null) {throw new BadInputParametersException("Передано пустое значение.");}

        userService.getUser(userId);

        List<Booking> response = null;

        if (Status.ALL.name().equals(state) || state == null) {

            return bookingRepository.findAllByItem_Owner_IdIsOrderByStartDateDesc(userId)
                    .stream()
                    .map(BookingMapper::toAllFieldsDto)
                    .collect(Collectors.toList());
        }

        if (Status.PAST.name().equals(state)) {

            response = bookingRepository.findBookingByItem_Owner_IdIsAndEndDateBeforeOrderByStartDateDesc(
                    userId, LocalDateTime.now()
            );
        }

        if (Status.CURRENT.name().equals(state)) {

            response = bookingRepository.findBookingByItem_Owner_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now()
            );
        }

        if (Status.FUTURE.name().equals(state)) {

            response = bookingRepository.findBookingByItem_Owner_IdIsAndStartDateAfterOrderByStartDateDesc(
                    userId, LocalDateTime.now()
            );
        }

        if (response == null && Arrays.stream(Status.values()).anyMatch(status -> status.name().equals(state))) {

            response = bookingRepository.findBookingByItem_Owner_IdIsAndStatusIsOrderByStartDateDesc(
                    userId, Status.valueOf(state)
            );
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
    public List<BookingAllFieldsDto> getAllUserItemsBookings(Long userId, String state, Integer from, Integer size) {

        if (userId == null) {throw new BadInputParametersException("Передано пустое значение.");}

        userService.getUser(userId);

        var page = PageRequestImpl.of(from, size, Sort.by("startDate").descending());

        List<Booking> response = null;

        if (Status.ALL.name().equals(state) || state == null) {

            if (page != null) {

                return bookingRepository.findAllByItem_Owner_IdIsOrderByStartDateDesc(userId, page)
                        .stream()
                        .map(BookingMapper::toAllFieldsDto)
                        .collect(Collectors.toList());
            } else {

                return bookingRepository.findAllByItem_Owner_IdIsOrderByStartDateDesc(userId)
                        .stream()
                        .map(BookingMapper::toAllFieldsDto)
                        .collect(Collectors.toList());
            }
        }

        if (Status.PAST.name().equals(state)) {

            if (page != null) {

                response = bookingRepository.findBookingByItem_Owner_IdIsAndEndDateBeforeOrderByStartDateDesc(
                        userId, LocalDateTime.now(), page
                );
            } else {

                response = bookingRepository.findBookingByItem_Owner_IdIsAndEndDateBeforeOrderByStartDateDesc(
                        userId, LocalDateTime.now()
                );
            }
        }

        if (Status.CURRENT.name().equals(state)) {

            if (page != null) {

                response = bookingRepository.findBookingByItem_Owner_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), page
                );
            } else {

                response = bookingRepository.findBookingByItem_Owner_IdIsAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now()
                );
            }
        }

        if (Status.FUTURE.name().equals(state)) {

            if (page != null) {

                response = bookingRepository.findBookingByItem_Owner_IdIsAndStartDateAfterOrderByStartDateDesc(
                        userId, LocalDateTime.now(), page
                );
            } else {

                response = bookingRepository.findBookingByItem_Owner_IdIsAndStartDateAfterOrderByStartDateDesc(
                        userId, LocalDateTime.now()
                );
            }
        }

        if (response == null && Arrays.stream(Status.values()).anyMatch(status -> status.name().equals(state))) {

            if (page!= null) {

                response = bookingRepository.findBookingByItem_Owner_IdIsAndStatusIsOrderByStartDateDesc(
                        userId, Status.valueOf(state), page
                );
            } else {

                response = bookingRepository.findBookingByItem_Owner_IdIsAndStatusIsOrderByStartDateDesc(
                        userId, Status.valueOf(state)
                );
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
