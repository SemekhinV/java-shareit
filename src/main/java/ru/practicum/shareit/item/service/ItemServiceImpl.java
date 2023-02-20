package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.validation.BadInputParametersException;
import ru.practicum.shareit.exception.validation.EntityExistException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final UserService userService;

    private final ItemDao itemDao;

    private void isAddValid(Long userId, ItemDto item) {

        if (item.getName() == null || item.getDescription() == null || item.getAvailable() == null) {
            throw new InvalidValueException("Ошибка добавления вещи, один из атрибутов не указан.");
        }

        if ("".equals(item.getName()) || "".equals(item.getDescription())) {
            throw new InvalidValueException("Ошибка создания новой вещи, значения некоторых полей пусты.");
        }

        if (userId == null) {
            throw new BadInputParametersException("Id пользователя не указан.");
        }

        userService.getUser(userId);
    }
    
    private Item isUpdateValid(Long userId, ItemDto item) {

        if (userId == null || item.getId() == null) {
            throw new BadInputParametersException("Id пользователя не указан.");
        }

        if (itemDao.getAll().stream().noneMatch(fromDb -> item.getId().equals(fromDb.getId()))) {
            throw new EntityExistException("Ошибка поиска вещи, " +
                    "запись с id = " + item.getId() + " не найдена.");
        }
                
        if (!userId.equals(itemDao.getItemById(item.getId()).getOwner())) {
            throw new EntityExistException("Ошибка обновления вещи, указан другой владелец.");
        }

        return itemDao.getItemById(item.getId());
    }

    private void isSearchValid(Long userId) {

        if (userId == null) {
            throw new BadInputParametersException("Переданы пустые для значения поиска.");
        }

        userService.getUser(userId);
    }

    @Override
    public ItemDto getItem(Long id) {

        if (id == null) {
            throw new BadInputParametersException("Указан неверный id вещи.");
        }

        if (itemDao.getAll().stream().anyMatch(item -> id.equals(item.getId()))) {

            return ItemMapper.toItemDto(itemDao.getItemById(id));
        } else {
            throw new EntityExistException("Ошибка поиска вещи, " +
                    "запись с id = " + id + " не найдена.");
        }
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto item) {

        isAddValid(userId, item);

        return ItemMapper.toItemDto(itemDao.addItem(
                Item.builder()
                        .id((long) itemDao.getAll().size() + 1)
                        .name(item.getName())
                        .description(item.getDescription())
                        .owner(userId)
                        .available(item.getAvailable())
                        .build()
                )
        );
    }

    @Override
    public List<ItemDto> getAllUsersItems(Long userId) {

        if (userId == null) {
            throw new BadInputParametersException("Переданы пустые для значения поиска.");
        }

        userService.getUser(userId);

        return itemDao
                .getAll()
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
                itemDao.addItem(reqItem)
        );
    }

    @Override
    public List<ItemDto> searchForItems(Long userId, String text) {

        if (text.isBlank()) {return List.of();}

        isSearchValid(userId);

        return itemDao
                .getAll()
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
}
