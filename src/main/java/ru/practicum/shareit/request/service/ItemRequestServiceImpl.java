package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.validation.BadInputParametersException;
import ru.practicum.shareit.exception.validation.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;

    private final ItemService itemService;

    private final UserService userService;

    @Override
    public ItemRequestDto getSingleItemRequest(Long userId, Long requestId) {

        if (userId == null || requestId == null) {
            throw new BadInputParametersException("Передано пустое значение.");
        }

        userService.getUser(userId);

        List<Item> item = itemService.getItemsByRequestId(requestId);

        ItemRequest response = requestRepository.findById(requestId).orElseThrow(
                () -> {throw new EntityNotFoundException("Запрос с id = " + requestId + " не найден.");}
        );

        return RequestMapper.toItemRequestDto(response, item);
    }

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) {
        return null;
    }
}
