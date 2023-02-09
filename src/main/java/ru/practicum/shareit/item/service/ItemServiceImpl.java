package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.validation.EntityExistException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

@Service
@Validated
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final UserService userService;

    private final ItemDao itemDao;

    private void isValid(Long id) {

    }

    @Override
    public ItemDto getItem(Long id) {

        return ItemMapper.toItemDto(

                itemDao.getItemById(id).orElseThrow(
                        () -> {
                            throw new EntityExistException("Ошибка поиска вещи, запись с id =" + id + " не найдена.");
                        }
                )
        );
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto item) {

        //userService.getUser(userId);            //Обращаемся к юзер сервису чтобы проверить существование пользователя

        return ItemMapper.toItemDto(
                itemDao.addItem(item, userId)
        );

    }

    @Override
    public ItemDto updateItem(ItemDto item) {

        return ItemMapper.toItemDto(
                itemDao.updateItem(item)
        );
    }
}
