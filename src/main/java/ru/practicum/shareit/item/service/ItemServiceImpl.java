package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.validation.BadInputParametersException;
import ru.practicum.shareit.exception.validation.EntityNotFoundException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;


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
    
    private Item isUpdateValid(Long userId, ItemDto item) {

        if (userId == null || item.getId() == null) {
            throw new BadInputParametersException("Id пользователя не указан.");
        }

        if (itemRepository.findAll().stream().noneMatch(fromDb -> item.getId().equals(fromDb.getId()))) {
            throw new EntityNotFoundException("Ошибка поиска вещи, " +
                    "запись с id = " + item.getId() + " не найдена.");
        }

        itemRepository.getReferenceById(item.getId());
        throw new EntityNotFoundException("Ошибка обновления вещи, указан другой владелец.");

    }

    private void isSearchValid(Long userId) {

        if (userId == null) {
            throw new BadInputParametersException("Переданы пустые для значения поиска.");
        }

        userService.getUser(userId);
    }

    @Override
    @Transactional
    public ItemDtoWithBookingAndComment getItem(Long id) {

        if (id == null) {throw new BadInputParametersException("Указан неверный id вещи.");}

        if (itemRepository.findAll().stream().anyMatch(item -> id.equals(item.getId()))) {

            return mapToItemDtoWithBookingAndComment(itemRepository.getReferenceById(id));
        } else {
            throw new EntityNotFoundException("Ошибка поиска вещи, " +
                    "запись с id = " + id + " не найдена.");
        }
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto item) {

        isAddValid(userId, item);

        User user = toUser(userService.getUser(userId));

        return ItemMapper.toItemDto(itemRepository.save(
                new Item(
                        (userId + 1),
                        (item.getName()),
                        (item.getDescription()),
                        (userId),
                        (item.getAvailable()),
                        0L)
                )
        );
    }

    @Override
    public List<ItemDto> getAllUsersItems(Long userId) {

        if (userId == null) {
            throw new BadInputParametersException("Переданы пустые для значения поиска.");
        }

        userService.getUser(userId);

        return itemRepository
                .findAll()
                .stream()
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(ItemDto item, Long userId) {

        Item reqItem = isUpdateValid(userId, item);

        if (item.getName() != null) {
            reqItem.setName(item.getName());
        } if (item.getDescription() != null) {
            reqItem.setDescription(item.getDescription());
        } if (item.getAvailable() != null) {
            reqItem.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(
                itemRepository.save(reqItem)
        );
    }

    @Override
    public List<ItemDto> searchForItems(Long userId, String text) {

        if (text.isBlank()) {return List.of();}

        isSearchValid(userId);

        return itemRepository
                .findAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getDescription()
                                .toLowerCase()
                                .contains(
                                        text
                                        .toLowerCase()
                                        .strip())
                )
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {

        return null;
    }
}
