package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.validation.EntityNotFoundException;
import ru.practicum.shareit.exception.validation.InvalidValueException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;

    private final ItemService itemService;

    private final UserService userService;

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new InvalidValueException("Описание запроса не может быть пустым.");
        }

        User user = UserMapper.toUser(userService.getUser(userId));

        ItemRequest request = ItemRequestMapper.toRequest(itemRequestDto);

        request.setRequester(user);

        request.setCreated(LocalDateTime.now());

        ItemRequest response = requestRepository.save(request);

        return ItemRequestMapper.mapToItemRequestDto(response);
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {

        userService.getUser(userId);

        List<ItemDto> items = itemService.getItemsByRequestId(requestId);

        ItemRequest response = requestRepository.findById(requestId).orElseThrow(
                () -> {
                    throw new EntityNotFoundException("Запрос с id = " + requestId + " не найден.");
                });

        return ItemRequestMapper.toItemRequestDto(response, items);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) {

        userService.getUser(userId);

        List<ItemRequest> itemRequests = requestRepository.findItemRequestsByRequesterIdIsOrderByCreatedDesc(userId);

        Map<Long,List<ItemDto>> items = itemService.findItemsByRequestsList(itemRequests)
                .stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequests
                .stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request, items.get(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Pageable page) {

        userService.getUser(userId);

        List<ItemRequest> itemRequests;

        itemRequests = requestRepository.findItemRequestsByRequesterIdIsNotOrderByCreatedDesc(userId, page);

        Map<Long,List<ItemDto>> items = itemService.findItemsByRequestsList(itemRequests)
                .stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequests
                .stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request, items.get(request.getId())))
                .collect(Collectors.toList());
    }
}
