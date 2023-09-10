package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.tools.PageableImpl;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    private static final String USER_ID = "X-Sharer-User-Id";

    private static final String ERROR_MESSAGE = "Переадно пустое значение id пользователя";

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = ERROR_MESSAGE) Long userId,
            @PathVariable Long requestId
    ) {

        return requestService.getItemRequestById(userId, requestId);
    }

    @PostMapping()
    public ItemRequestDto addItemRequest(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = ERROR_MESSAGE) Long userId,
            @RequestBody ItemRequestDto itemRequestDto
    ) {

        return requestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping()
    public List<ItemRequestDto> getItemRequests(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = ERROR_MESSAGE) Long userId
    ) {

        return requestService.getAllItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(
            @RequestHeader(value = USER_ID, required = false) @NotNull(message = ERROR_MESSAGE) Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {

        return requestService.getAllItemRequests(
                userId, PageableImpl.of(from, size, Sort.by("created").descending())
        );
    }
}
