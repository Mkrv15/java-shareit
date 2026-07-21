package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestMapper mapper;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private Long userId;
    private Long requestId;
    private Long itemId;
    private User user;
    private ItemRequest itemRequest;
    private Item item;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;

    @BeforeEach
    void setUp() {
        userId = 1L;
        requestId = 1L;
        itemId = 1L;

        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");

        item = new Item();
        item.setId(itemId);
        item.setName("Drill");
        item.setDescription("Professional drill");
        item.setAvailable(true);
        item.setUserId(userId);
        item.setRequestId(requestId);

        itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");

        itemRequestDtoResponse = ItemRequestDtoResponse.builder()
                .id(requestId)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateRequestWhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.mapToItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(mapper.mapToItemRequestDtoResponse(any(ItemRequest.class))).thenReturn(itemRequestDtoResponse);

        ItemRequestDtoResponse result = itemRequestService.createItemRequest(itemRequestDto, userId);

        assertNotNull(result);
        assertEquals(itemRequestDtoResponse.getId(), result.getId());
        assertEquals(itemRequestDtoResponse.getDescription(), result.getDescription());

        verify(userRepository).findById(userId);
        verify(mapper).mapToItemRequest(any(ItemRequestDto.class));
        verify(itemRequestRepository).save(any(ItemRequest.class));
        verify(mapper).mapToItemRequestDtoResponse(any(ItemRequest.class));
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFoundForCreate() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestDto, userId)
        );

        assertEquals("Пользователя с id = " + userId + " нет", exception.getMessage());

        verify(userRepository).findById(userId);
        verifyNoInteractions(itemRequestRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturnPrivateRequestsForUser() {
        int from = 0;
        int size = 10;
        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(eq(userId), any(Pageable.class)))
                .thenReturn(itemRequests);
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of(item));

        List<RequestDtoResponseWithMD> result = itemRequestService.getPrivateRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestId, result.get(0).getId());
        assertEquals("Need a drill", result.get(0).getDescription());
        assertNotNull(result.get(0).getItems());
        assertEquals(1, result.get(0).getItems().size());

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findAllByRequesterId(eq(userId), any(Pageable.class));
        verify(itemRepository).findByRequestIdIn(anyList());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoPrivateRequests() {
        int from = 0;
        int size = 10;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(eq(userId), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<RequestDtoResponseWithMD> result = itemRequestService.getPrivateRequests(userId, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findAllByRequesterId(eq(userId), any(Pageable.class));
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFoundForPrivateRequests() {
        int from = 0;
        int size = 10;

        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getPrivateRequests(userId, from, size)
        );

        assertEquals("Пользователя с id = " + userId + " нет", exception.getMessage());

        verify(userRepository).existsById(userId);
        verifyNoInteractions(itemRequestRepository);
    }

    @Test
    void shouldReturnOtherRequestsForUser() {
        int from = 0;
        int size = 10;
        Page<ItemRequest> itemRequestPage = new PageImpl<>(List.of(itemRequest));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNot(eq(userId), any(Pageable.class)))
                .thenReturn(itemRequestPage);
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of(item));

        List<RequestDtoResponseWithMD> result = itemRequestService.getOtherRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestId, result.get(0).getId());
        assertEquals("Need a drill", result.get(0).getDescription());
        assertNotNull(result.get(0).getItems());
        assertEquals(1, result.get(0).getItems().size());

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findAllByRequesterIdNot(eq(userId), any(Pageable.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoOtherRequestsExist() {
        int from = 0;
        int size = 10;
        Page<ItemRequest> emptyPage = Page.empty();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNot(eq(userId), any(Pageable.class)))
                .thenReturn(emptyPage);

        List<RequestDtoResponseWithMD> result = itemRequestService.getOtherRequests(userId, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findAllByRequesterIdNot(eq(userId), any(Pageable.class));
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFoundForOtherRequests() {
        int from = 0;
        int size = 10;

        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getOtherRequests(userId, from, size)
        );

        assertEquals("Пользователя с id = " + userId + " нет", exception.getMessage());

        verify(userRepository).existsById(userId);
        verifyNoInteractions(itemRequestRepository);
    }

    @Test
    void shouldReturnRequestWhenExists() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of(item));

        RequestDtoResponseWithMD result = itemRequestService.getItemRequest(userId, requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(itemId, result.getItems().get(0).getId());

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).findByRequestIdIn(anyList());
    }

    @Test
    void shouldThrowNotFoundWhenRequestDoesNotExist() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequest(userId, requestId)
        );

        assertEquals("Запроса с id = " + requestId + " нет", exception.getMessage());

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFoundForGetRequest() {
        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequest(userId, requestId)
        );

        assertEquals("Пользователя с id = " + userId + " нет", exception.getMessage());

        verify(userRepository).existsById(userId);
        verifyNoInteractions(itemRequestRepository);
    }

    @Test
    void shouldBuildResponseWithItems() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of(item));

        RequestDtoResponseWithMD result = itemRequestService.getItemRequest(userId, requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());

        ItemDataForRequestDto itemData = result.getItems().get(0);
        assertEquals(itemId, itemData.getId());
        assertEquals("Drill", itemData.getName());
        assertEquals("Professional drill", itemData.getDescription());
        assertTrue(itemData.getAvailable());
        assertEquals(requestId, itemData.getRequestId());

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).findByRequestIdIn(anyList());
    }

    @Test
    void shouldHandleRequestWithoutItems() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        RequestDtoResponseWithMD result = itemRequestService.getItemRequest(userId, requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());

        verify(itemRepository).findByRequestIdIn(anyList());
    }

    @Test
    void shouldReturnPrivateRequestsWithPagination() {
        int from = 5;
        int size = 10;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(eq(userId), any(Pageable.class)))
                .thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        List<RequestDtoResponseWithMD> result = itemRequestService.getPrivateRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(itemRequestRepository).findAllByRequesterId(eq(userId), any(Pageable.class));
        verify(itemRepository).findByRequestIdIn(anyList());
    }
}