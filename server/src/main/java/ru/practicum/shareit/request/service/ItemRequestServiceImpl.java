package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemDataForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requests;
    private final UserRepository users;
    private final ItemRequestMapper mapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        User user = users.findById(requesterId).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + requesterId + " нет"));
        ItemRequest newItemRequest = mapper.mapToItemRequest(itemRequestDto);
        newItemRequest.setRequester(user);
        newItemRequest.setCreated(LocalDateTime.now());
        return mapper.mapToItemRequestDtoResponse(requests.save(newItemRequest));
    }

    @Override
    public List<RequestDtoResponseWithMD> getPrivateRequests(Long requesterId, Integer from, Integer size) {
        if (!users.existsById(requesterId)) {
            throw new NotFoundException("Пользователя с id = " + requesterId + " нет");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> itemRequests = requests.findAllByRequesterId(requesterId, pageable);

        return itemRequests.stream()
                .map(this::buildResponseWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDtoResponseWithMD> getOtherRequests(Long requesterId, Integer from, Integer size) {
        if (!users.existsById(requesterId)) {
            throw new NotFoundException("Пользователя с id = " + requesterId + " нет");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> page = requests.findAllByRequesterIdNot(requesterId, pageable);
        return page.getContent().stream()
                .map(this::buildResponseWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDtoResponseWithMD getItemRequest(Long userId, Long requestId) {
        if (!users.existsById(userId)) {
            throw new NotFoundException("Пользователя с id = " + userId + " нет");
        }
        ItemRequest request = requests.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запроса с id = " + requestId + " нет"));
        return buildResponseWithItems(request);
    }

    private RequestDtoResponseWithMD buildResponseWithItems(ItemRequest request) {
        List<ItemDataForRequestDto> items = itemRepository.findByRequestId(request.getId())
                .stream()
                .map(item -> ItemDataForRequestDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .requestId(request.getId())
                        .build())
                .collect(Collectors.toList());

        return RequestDtoResponseWithMD.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }
}
