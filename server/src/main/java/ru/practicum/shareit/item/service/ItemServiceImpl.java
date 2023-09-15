package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.validation.BadInputParametersException;
import ru.practicum.shareit.exception.validation.EntityNotFoundException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserService userService;

    private final ItemRepository itemRepository;

    private final CommentRepository commentRepository;

    private BookingService bookingService;

    private void isAddValid(Long userId, ItemDto item) {

        if (item.getName() == null || item.getDescription() == null || item.getAvailable() == null) {
            throw new InvalidValueException("Ошибка добавления вещи, один из атрибутов не указан.");
        }

        if ("".equals(item.getName()) || "".equals(item.getDescription())) {
            throw new InvalidValueException("Ошибка создания новой вещи, заполнены не все поля.");
        }
    }

    @Override
    @Transactional
    public ItemDtoWithBookingAndComment getItem(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {
                    throw new EntityNotFoundException("Вещь с указанным id не найдена.");
                });

        List<CommentDto> comments = getAllItemComments(itemId);

        List<BookingAllFieldsDto> bookings = bookingService.getBookingsByItemId(itemId, userId);

        return ItemMapper.mapToItemDtoWithBookingAndComment(
                item,
                getLastBooking(bookings),
                getFutureBooking(bookings),
                comments
        );
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto, ItemRequestDto itemRequest) {

        isAddValid(userId, itemDto);

        User user = UserMapper.toUser(userService.getUser(userId));

        Item item = ItemMapper.toItem(itemDto);

        item.setOwner(user);

        if (itemRequest != null) {

            item.setRequest(ItemRequestMapper.toRequest(
                    itemRequest, userService.getUser(itemRequest.getUserId())));
        }

        Item fromDb = itemRepository.save(item);

        return ItemMapper.toItemDto(fromDb);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long userId) {

        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> {
                    throw new EntityNotFoundException("Вещь с указанным id не найдена.");
                });

        if (!item.getOwner().getId().equals(userId)) {
            throw new BadInputParametersException("Указанный пользователь не является хозяином.");
        }

        if (itemDto.getName() != null) {

            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {

            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {

            item.setAvailable(itemDto.getAvailable());
        }

        Item fromDb = itemRepository.save(item);

        return ItemMapper.toItemDto(fromDb);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {
                    throw new EntityNotFoundException("Вещь с id = " + itemId + " не найдена.");
                });

        itemRepository.delete(item);
    }

    @Override
    @Transactional
    public List<ItemDto> getAllUsersItems(Long userId, Pageable page) {

        userService.getUser(userId);

        List<Item> userItems;

        userItems = itemRepository.findAllByOwnerIdIs(
                userId,
                page
        );

        return getAllBookingsAndComments(userId, userItems, page);
    }

    private List<ItemDto> getAllBookingsAndComments(Long userId, List<Item> userItems, Pageable page) {

        Map<Long, List<BookingAllFieldsDto>> bookings = bookingService
                .getAllUserItemsBookings(
                        userId,
                        null,
                        page)
                .stream()
                .collect(Collectors.groupingBy(bookingAllFieldsDto -> bookingAllFieldsDto.getItem().getId()));

        Map<Long, List<CommentDto>> comments = getAllComments()
                .stream()
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        return userItems
                .stream()
                .map(item -> ItemMapper.mapToItemDtoWithBookingAndComment(
                        item,
                        getLastBooking(bookings.get(item.getId())),
                        getFutureBooking(bookings.get(item.getId())),
                        comments.get(item.getId())
                ))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public List<ItemDto> searchForItems(Long userId, String text, Pageable page) {

        if (text.isBlank()) {

            return List.of();
        }

        userService.getUser(userId);

        List<Item> items;

        items = itemRepository.searchForItems(text, page);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {

        if (commentDto.getText() == null || commentDto.getText().isBlank()) {

            throw new InvalidValueException("Комментарий не может быть пустым.");
        }

        User user = UserMapper.toUser(userService.getUser(userId));

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {
                    throw new EntityNotFoundException("Вещь с id = " + itemId + " не найдена.");
                });

        List<BookingAllFieldsDto> bookings = bookingService.getAllBookingsOfCurrentUser(
                userId, "PAST",
                PageRequest.of(1, 1, Sort.by("startDate").descending())
        );

        if (bookings.isEmpty() || bookings.stream().noneMatch(booking -> booking.getItem().getId().equals(itemId))) {

            throw new InvalidValueException("Нельзя оставить отзыв не арендовав вещь.");
        }

        Comment comment = CommentMapper.toComment(commentDto);

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment response = commentRepository.save(comment);

        return CommentMapper.toCommentDto(response);
    }

    @Override
    @Transactional
    public List<CommentDto> getAllComments() {

        return commentRepository
                .findAll()
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CommentDto> getAllItemComments(Long itemId) {

        return commentRepository.findAllByItemIdIsOrderByCreated(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> getItemsByRequestId(Long requestId) {

        return itemRepository
                .findAllByRequestIdIs(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> findItemsByRequestsList(List<ItemRequest> requests) {

        return itemRepository
                .findAllByRequestIn(requests)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private BookingAllFieldsDto getFutureBooking(List<BookingAllFieldsDto> bookings) {

        if (bookings != null) {

            return bookings
                    .stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(BookingAllFieldsDto::getEnd))
                    .orElse(null);
        } else {

            return null;
        }
    }

    private BookingAllFieldsDto getLastBooking(List<BookingAllFieldsDto> bookings) {

        if (bookings != null) {

            return bookings
                    .stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(BookingAllFieldsDto::getEnd))
                    .orElse(null);
        } else {

            return null;
        }
    }

}
