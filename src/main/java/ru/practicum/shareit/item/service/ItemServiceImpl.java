package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.validation.EntityExistException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.UserDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

@Service
@Validated
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final UserService userService;

    private final ItemDao itemDao;

    private void isValid(Long userId, UserDto item) {

        userService.getUser(userId);

        if (!userId.equals(itemDao.getItemById(item.getId()).get().getOwner())) {
            throw new InvalidValueException("Идентификатор владельца вещи не совпадает с переданным значением.");
        }
    }

    @Override
    public UserDto getItem(Long id) {

        return ItemMapper.toItemDto(

                itemDao.getItemById(id).orElseThrow(
                        () -> {
                            throw new EntityExistException("Ошибка поиска вещи, запись с id =" + id + " не найдена.");
                        }
                )
        );
    }

    @Override
    public UserDto addItem(Long userId, UserDto item) {

        userService.getUser(userId);

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
    public UserDto updateItem(UserDto item, Long userId) {

        isValid(userId, item);

        Item updatedItem = itemDao.getItemById(item.getId()).orElseThrow(
                () -> {throw new EntityExistException("item not found");}
        );

        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        } if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        } if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(
                itemDao.addItem(updatedItem)
        );
    }
}
