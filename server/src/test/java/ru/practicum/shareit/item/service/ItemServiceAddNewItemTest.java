package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceAddNewItemTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Long userId;
    private Long itemId;
    private Long requestId;
    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 2L;
        requestId = 5L;

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

        itemDto = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .requestId(requestId)
                .build();
    }

    @Test
    void shouldCreateItemWhenValidData() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(item);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);

        ItemDto result = itemService.addItem(userId, itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getRequestId(), result.getRequestId());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemMapper, times(1)).convertToDto(any(Item.class));
    }

    @Test
    void shouldCreateItemWhenRequestIdIsNull() {
        item.setRequestId(null);
        ItemDto itemDtoWithoutRequest = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .requestId(null)
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(item);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDtoWithoutRequest);

        ItemDto result = itemService.addItem(userId, itemDtoWithoutRequest);

        assertNotNull(result);
        assertEquals(itemDtoWithoutRequest.getName(), result.getName());
        assertEquals(itemDtoWithoutRequest.getDescription(), result.getDescription());
        assertEquals(itemDtoWithoutRequest.getAvailable(), result.getAvailable());
        assertNull(result.getRequestId());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemMapper, times(1)).convertToDto(any(Item.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(item);
        when(userService.getUserById(userId)).thenThrow(new NotFoundException("Пользователь с id " + userId + " не найден"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addItem(userId, itemDto)
        );

        assertEquals("Пользователь с id " + userId + " не найден", exception.getMessage());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verifyNoInteractions(itemRepository);
        verify(itemMapper, never()).convertToDto(any(Item.class));
    }

    @Test
    void shouldCreateItemWithCorrectOwnerId() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(item);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(itemId);
            return savedItem;
        });
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);

        ItemDto result = itemService.addItem(userId, itemDto);

        assertNotNull(result);
        verify(itemRepository).save(argThat(savedItem ->
                savedItem.getUserId() == userId
        ));

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldHandleItemWithoutRequestId() {
        ItemDto itemDtoWithoutRequest = ItemDto.builder()
                .name("Hammer")
                .description("Heavy hammer")
                .available(true)
                .requestId(null)
                .build();

        Item itemWithoutRequest = new Item();
        itemWithoutRequest.setId(3L);
        itemWithoutRequest.setName("Hammer");
        itemWithoutRequest.setDescription("Heavy hammer");
        itemWithoutRequest.setAvailable(true);
        itemWithoutRequest.setUserId(userId);
        itemWithoutRequest.setRequestId(null);

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(itemWithoutRequest);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(itemWithoutRequest);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDtoWithoutRequest);

        ItemDto result = itemService.addItem(userId, itemDtoWithoutRequest);

        assertNotNull(result);
        assertEquals("Hammer", result.getName());
        assertNull(result.getRequestId());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldCreateItemWithAvailableFalse() {
        ItemDto itemDtoNotAvailable = ItemDto.builder()
                .name("Broken Drill")
                .description("Broken drill")
                .available(false)
                .requestId(null)
                .build();

        Item itemNotAvailable = new Item();
        itemNotAvailable.setId(4L);
        itemNotAvailable.setName("Broken Drill");
        itemNotAvailable.setDescription("Broken drill");
        itemNotAvailable.setAvailable(false);
        itemNotAvailable.setUserId(userId);
        itemNotAvailable.setRequestId(null);

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(itemNotAvailable);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(itemNotAvailable);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDtoNotAvailable);

        ItemDto result = itemService.addItem(userId, itemDtoNotAvailable);

        assertNotNull(result);
        assertFalse(result.getAvailable());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldSetUserIdFromUserService() {
        // given
        Long actualUserId = 10L;
        User actualUser = new User();
        actualUser.setId(actualUserId);
        actualUser.setName("Actual User");

        ItemDto inputDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        Item itemToSave = new Item();
        itemToSave.setName("Test Item");
        itemToSave.setDescription("Test Description");
        itemToSave.setAvailable(true);

        Item savedItem = new Item();
        savedItem.setId(100L);
        savedItem.setName("Test Item");
        savedItem.setDescription("Test Description");
        savedItem.setAvailable(true);
        savedItem.setUserId(actualUserId);

        ItemDto resultDto = ItemDto.builder()
                .id(100L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(itemToSave);
        when(userService.getUserById(actualUserId)).thenReturn(actualUser);
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(resultDto);

        ItemDto result = itemService.addItem(actualUserId, inputDto);

        assertNotNull(result);
        assertEquals(100L, result.getId());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(actualUserId);
        verify(itemRepository).save(argThat(itemArg ->
                itemArg.getUserId() == actualUserId
        ));
    }

    @Test
    void shouldThrowExceptionWhenUserServiceThrows() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(item);
        when(userService.getUserById(userId)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> itemService.addItem(userId, itemDto));

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verifyNoInteractions(itemRepository);
        verify(itemMapper, never()).convertToDto(any(Item.class));
    }

    @Test
    void shouldNotCallRepositoryWhenUserNotFound() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(item);
        when(userService.getUserById(userId)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemService.addItem(userId, itemDto));

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, never()).save(any(Item.class));
        verify(itemMapper, never()).convertToDto(any(Item.class));
    }
}