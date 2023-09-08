package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.validation.BadInputParametersException;
import ru.practicum.shareit.exception.validation.EntityNotFoundException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.tools.PageRequestImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService{

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

        if (userId == null) {
            throw new BadInputParametersException("Id пользователя не указан.");
        }
    }

    private void isSearchValid(Long userId) {

        if (userId == null) {
            throw new BadInputParametersException("Переданы пустые для значения поиска.");
        }

        userService.getUser(userId);
    }

    private void isAddCommentValid(CommentDto commentDto, Long userId, Long itemId) {

        if (itemId == null || userId == null) {
            throw new BadInputParametersException("Передано пустое значение.");
        }

        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new InvalidValueException("Комментарий не может быть пустым.");
        }
    }

    @Override
    @Transactional
    public ItemDtoWithBookingAndComment getItem(Long itemId, Long userId) {

        if (itemId == null || userId == null) {throw new BadInputParametersException("Указан неверный id вещи.");}

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {throw new EntityNotFoundException("Вещь с указанным id не найдена.");}
        );

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

        if (userId == null) {throw new BadInputParametersException("Передано пустое значение id пользователя.");}

        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> {throw new EntityNotFoundException("Вещь с указанным id не найдена.");}
        );

        if (!item.getOwner().getId().equals(userId)) {
            throw new BadInputParametersException("Указанный пользователь не является хозяином.");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        } if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        } if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item fromDb = itemRepository.save(item);

        return ItemMapper.toItemDto(fromDb);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {

        if (itemId == null) {throw new BadInputParametersException("Передано пустое значение.");}

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {throw new EntityNotFoundException("Вещь с id = " + itemId + " не найдена.");}
        );

        itemRepository.delete(item);
    }

    @Override
    @Transactional
    public List<ItemDto> getAllUsersItems(Long userId, Integer from, Integer size) {

        if (userId == null) {
            throw new BadInputParametersException("Переданы пустые для значения поиска.");
        }

        userService.getUser(userId);

        List<Item> userItems;

        var page = PageRequestImpl.of(from, size, Sort.by("created").descending());

        if (page == null) {

            userItems = itemRepository.findAllByOwner_IdIs(userId);
        } else {

            userItems = itemRepository.findAllByOwner_IdIs(userId, page);
        }

        return getAllBookingsAndComments(userId, userItems);
    }

    private List<ItemDto> getAllBookingsAndComments(Long userId, List<Item> userItems) {

        Map<Long, List<BookingAllFieldsDto>> bookings = bookingService
                .getAllUserItemsBookings(userId, null, null, null)
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
    public List<ItemDto> searchForItems(Long userId, String text, Integer from, Integer size) {

        if (text.isBlank()) {return List.of();}

        isSearchValid(userId);

        List<Item> items;

        var page = PageRequestImpl.of(from, size, Sort.by("created").descending());

        if (page == null) {

            items = itemRepository.searchForItems(text);
        } else {

            items = itemRepository.searchForItems(text, page);
        }

        return itemRepository
                .searchForItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {

        isAddCommentValid(commentDto, userId, itemId);

        User user = UserMapper.toUser(userService.getUser(userId));

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {throw new EntityNotFoundException("Вещь с id = " + itemId + " не найдена.");}
        );

        List<BookingAllFieldsDto> bookings = bookingService.getAllBookingsOfCurrentUser(userId, "PAST", null, null);

        //Проверка на взятие именно этой вещи в аренду пользователем
        if (bookings.isEmpty() ||
                bookings
                .stream()
                .noneMatch(booking -> booking.getItem().getId().equals(itemId))
        ) {
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

        return commentRepository.findAllByItem_IdIsOrderByCreated(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> getItemsByRequestId(Long requestId) {

        if (requestId == null ) {
            throw new BadInputParametersException("Передано пустое значение.");
        }

        return itemRepository
                .findAllByRequest_IdIs(requestId)
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
