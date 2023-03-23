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
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
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

    @Override
    @Transactional
    public ItemDtoWithBookingAndComment getItem(Long id) {

        if (id == null) {throw new BadInputParametersException("Указан неверный id вещи.");}

        Item item = itemRepository.findById(id).orElseThrow(
                () -> {throw new EntityNotFoundException("Вещь с указанным id не найдена.");}
        );

    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {

        isAddValid(userId, itemDto);

        User user = UserMapper.toUser(userService.getUser(userId));

        Item item = ItemMapper.toItem(itemDto);

        item.setOwner(user);

        Item fromDb = itemRepository.save(item);

        return ItemMapper.toItemDto(fromDb);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId) {

        if (userId == null) {throw new BadInputParametersException("Передано пустое значение id пользователя.");}

        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> {throw new EntityNotFoundException("Вещь с указанным id не найдена.");}
        );

        if (!item.getOwner().getId().equals(userId)) {
            throw new InvalidValueException("Указанный пользователь не является хозяином.");
        }

        if (itemDto.getName() != null) {
            item.setName(item.getName());
        } if (itemDto.getDescription() != null) {
            item.setDescription(item.getDescription());
        } if (itemDto.getAvailable() != null) {
            item.setAvailable(item.getAvailable());
        }

        Item fromDb = itemRepository.save(item);

        return ItemMapper.toItemDto(fromDb);
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

    @Override
    public List<CommentDto> getAllComments() {

        return commentRepository
                .findAll()
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllItemComments(Long id) {

        return null;
    }
}
