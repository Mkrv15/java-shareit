package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Headers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse createRequest(@RequestHeader(Headers.USER_ID_HEADER) Long requesterId,
                                                @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<RequestDtoResponseWithMD> getPrivateRequests(
            @RequestHeader(Headers.USER_ID_HEADER) Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getPrivateRequests(requesterId, from, size);
    }

    @GetMapping("all")
    public List<RequestDtoResponseWithMD> getOtherRequests(
            @RequestHeader(Headers.USER_ID_HEADER) Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getOtherRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public RequestDtoResponseWithMD getItemRequest(
            @RequestHeader(Headers.USER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }
}
