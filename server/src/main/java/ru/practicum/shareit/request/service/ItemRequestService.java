package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId);

    List<RequestDtoResponseWithMD> getPrivateRequests(Long requesterId, Integer from, Integer size);

    List<RequestDtoResponseWithMD> getOtherRequests(Long requesterId, Integer from, Integer size);

    RequestDtoResponseWithMD getItemRequest(Long userId, Long requestId);
}
