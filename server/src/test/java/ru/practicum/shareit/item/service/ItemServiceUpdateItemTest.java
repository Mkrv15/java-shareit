package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUpdateItemTest {

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
    private Long ownerId;
    private Long requestId;
    private User user;
    private User owner;
    private Item existingItem;
    private ItemDto updateDto;
    private ItemDto resultDto;
    private Item updatedItem;

    @BeforeEach
    void setUp() {
        userId = 1L;
        ownerId = 1L;
        itemId = 10L;
        requestId = 5L;

        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("user@example.com");

        owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Drill");
        existingItem.setDescription("Old description");
        existingItem.setAvailable(true);
        existingItem.setUserId(ownerId);
        existingItem.setRequestId(requestId);

        updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName("New Drill");
        updatedItem.setDescription("New description");
        updatedItem.setAvailable(false);
        updatedItem.setUserId(ownerId);
        updatedItem.setRequestId(requestId);

        updateDto = ItemDto.builder()
                .name("New Drill")
                .description("New description")
                .available(false)
                .requestId(requestId)
                .build();

        resultDto = ItemDto.builder()
                .id(itemId)
                .name("New Drill")
                .description("New description")
                .available(false)
                .requestId(requestId)
                .build();
    }

    @Test
    void shouldUpdateItemWhenAllFieldsAreValid() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(updatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(resultDto);

        ItemDto result = itemService.updateItem(userId, itemId, updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getDescription(), result.getDescription());
        assertEquals(updateDto.getAvailable(), result.getAvailable());
        assertEquals(updateDto.getRequestId(), result.getRequestId());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemMapper, times(1)).convertToDto(any(Item.class));
    }

    @Test
    void shouldUpdateItemWhenOnlyNameProvided() {
        ItemDto partialUpdate = ItemDto.builder()
                .name("New Name Only")
                .build();

        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(itemId);
        partialUpdatedItem.setName("New Name Only");
        partialUpdatedItem.setDescription(existingItem.getDescription());
        partialUpdatedItem.setAvailable(existingItem.getAvailable());
        partialUpdatedItem.setUserId(ownerId);
        partialUpdatedItem.setRequestId(existingItem.getRequestId());

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name("New Name Only")
                .description(existingItem.getDescription())
                .available(existingItem.getAvailable())
                .requestId(existingItem.getRequestId())
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(partialUpdatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(partialUpdatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, partialUpdate);

        assertNotNull(result);
        assertEquals("New Name Only", result.getName());
        assertEquals(existingItem.getDescription(), result.getDescription());
        assertEquals(existingItem.getAvailable(), result.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldUpdateItemWhenOnlyDescriptionProvided() {
        ItemDto partialUpdate = ItemDto.builder()
                .description("New Description Only")
                .build();

        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(itemId);
        partialUpdatedItem.setName(existingItem.getName());
        partialUpdatedItem.setDescription("New Description Only");
        partialUpdatedItem.setAvailable(existingItem.getAvailable());
        partialUpdatedItem.setUserId(ownerId);
        partialUpdatedItem.setRequestId(existingItem.getRequestId());

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name(existingItem.getName())
                .description("New Description Only")
                .available(existingItem.getAvailable())
                .requestId(existingItem.getRequestId())
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(partialUpdatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(partialUpdatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, partialUpdate);

        assertNotNull(result);
        assertEquals(existingItem.getName(), result.getName());
        assertEquals("New Description Only", result.getDescription());
        assertEquals(existingItem.getAvailable(), result.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldUpdateItemWhenOnlyAvailableProvided() {
        ItemDto partialUpdate = ItemDto.builder()
                .available(false)
                .build();

        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(itemId);
        partialUpdatedItem.setName(existingItem.getName());
        partialUpdatedItem.setDescription(existingItem.getDescription());
        partialUpdatedItem.setAvailable(false);
        partialUpdatedItem.setUserId(ownerId);
        partialUpdatedItem.setRequestId(existingItem.getRequestId());

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name(existingItem.getName())
                .description(existingItem.getDescription())
                .available(false)
                .requestId(existingItem.getRequestId())
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(partialUpdatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(partialUpdatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, partialUpdate);

        assertNotNull(result);
        assertEquals(existingItem.getName(), result.getName());
        assertEquals(existingItem.getDescription(), result.getDescription());
        assertFalse(result.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldUpdateItemWhenAllFieldsNull() {
        ItemDto emptyUpdate = ItemDto.builder().build();

        Item emptyItem = new Item();

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name(existingItem.getName())
                .description(existingItem.getDescription())
                .available(existingItem.getAvailable())
                .requestId(existingItem.getRequestId())
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(emptyItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, emptyUpdate);

        assertNotNull(result);
        assertEquals(existingItem.getName(), result.getName());
        assertEquals(existingItem.getDescription(), result.getDescription());
        assertEquals(existingItem.getAvailable(), result.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldUpdateItemWithNewRequestId() {
        Long newRequestId = 99L;
        ItemDto updateWithRequest = ItemDto.builder()
                .requestId(newRequestId)
                .build();

        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(itemId);
        partialUpdatedItem.setName(existingItem.getName());
        partialUpdatedItem.setDescription(existingItem.getDescription());
        partialUpdatedItem.setAvailable(existingItem.getAvailable());
        partialUpdatedItem.setUserId(ownerId);
        partialUpdatedItem.setRequestId(newRequestId);

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name(existingItem.getName())
                .description(existingItem.getDescription())
                .available(existingItem.getAvailable())
                .requestId(newRequestId)
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(partialUpdatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(partialUpdatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, updateWithRequest);

        assertNotNull(result);
        assertEquals(newRequestId, result.getRequestId());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFound() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(updatedItem);
        when(userService.getUserById(userId)).thenThrow(new NotFoundException("Пользователь с id " + userId + " не найден"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(userId, itemId, updateDto)
        );

        assertEquals("Пользователь с id " + userId + " не найден", exception.getMessage());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void shouldThrowNotFoundWhenItemNotFound() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(updatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(userId, itemId, updateDto)
        );

        assertEquals("Вещь с id " + itemId + " не найдена", exception.getMessage());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void shouldThrowNotFoundWhenNonOwnerUpdates() {
        Long nonOwnerId = 99L;
        User nonOwner = new User();
        nonOwner.setId(nonOwnerId);
        nonOwner.setName("Non Owner");

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(updatedItem);
        when(userService.getUserById(nonOwnerId)).thenReturn(nonOwner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(nonOwnerId, itemId, updateDto)
        );

        assertEquals(String.format("У пользователя с id %s не найдена вещь с id %s", nonOwnerId, itemId),
                exception.getMessage());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(nonOwnerId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void shouldThrowNotFoundWhenItemBelongsToAnotherUser() {
        Long anotherUserId = 88L;
        User anotherUser = new User();
        anotherUser.setId(anotherUserId);
        anotherUser.setName("Another User");

        Item anotherItem = new Item();
        anotherItem.setId(20L);
        anotherItem.setName("Another Item");
        anotherItem.setUserId(anotherUserId);

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(updatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(20L)).thenReturn(Optional.of(anotherItem));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(userId, 20L, updateDto)
        );

        assertEquals(String.format("У пользователя с id %s не найдена вещь с id %s", userId, 20L),
                exception.getMessage());

        verify(itemMapper, times(1)).convertFromDto(any(ItemDto.class));
        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(20L);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void shouldNotUpdateRequestIdWhenNull() {
        Long originalRequestId = existingItem.getRequestId();
        ItemDto updateWithoutRequest = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(itemId);
        partialUpdatedItem.setName("Updated Name");
        partialUpdatedItem.setDescription("Updated Description");
        partialUpdatedItem.setAvailable(false);
        partialUpdatedItem.setUserId(ownerId);
        partialUpdatedItem.setRequestId(originalRequestId);

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .requestId(originalRequestId)
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(partialUpdatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(partialUpdatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, updateWithoutRequest);

        assertNotNull(result);
        assertEquals(originalRequestId, result.getRequestId());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldUpdateItemAndPreserveFieldsNotInDto() {
        ItemDto partialUpdate = ItemDto.builder()
                .name("New Name")
                .build();

        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(itemId);
        partialUpdatedItem.setName("New Name");
        partialUpdatedItem.setDescription(existingItem.getDescription());
        partialUpdatedItem.setAvailable(existingItem.getAvailable());
        partialUpdatedItem.setUserId(ownerId);
        partialUpdatedItem.setRequestId(existingItem.getRequestId());

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name("New Name")
                .description(existingItem.getDescription())
                .available(existingItem.getAvailable())
                .requestId(existingItem.getRequestId())
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(partialUpdatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(partialUpdatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, partialUpdate);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals(existingItem.getDescription(), result.getDescription());
        assertEquals(existingItem.getAvailable(), result.getAvailable());
        assertEquals(existingItem.getRequestId(), result.getRequestId());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldCallUserServiceAndItemRepositoryInCorrectOrder() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(updatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(resultDto);

        itemService.updateItem(userId, itemId, updateDto);

        InOrder inOrder = inOrder(itemMapper, userService, itemRepository);
        inOrder.verify(itemMapper).convertFromDto(any(ItemDto.class));  // 1
        inOrder.verify(userService).getUserById(userId);                // 2
        inOrder.verify(itemRepository).findById(itemId);               // 3
        inOrder.verify(itemRepository).save(any(Item.class));          // 4
        inOrder.verify(itemMapper).convertToDto(any(Item.class));      // 5
    }

    @Test
    void shouldNotSaveWhenValidationFails() {
        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(updatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userId, itemId, updateDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void shouldUpdateItemWithEmptyStringName() {
        ItemDto updateWithEmptyName = ItemDto.builder()
                .name("")
                .description("New Description")
                .available(false)
                .build();

        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(itemId);
        partialUpdatedItem.setName("");
        partialUpdatedItem.setDescription("New Description");
        partialUpdatedItem.setAvailable(false);
        partialUpdatedItem.setUserId(ownerId);
        partialUpdatedItem.setRequestId(existingItem.getRequestId());

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name(existingItem.getName())
                .description("New Description")
                .available(false)
                .requestId(existingItem.getRequestId())
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(partialUpdatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(partialUpdatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, updateWithEmptyName);

        assertNotNull(result);
        assertEquals(existingItem.getName(), result.getName());
        assertEquals("New Description", result.getDescription());
        assertFalse(result.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldUpdateItemWhenAvailableIsFalse() {
        existingItem.setAvailable(true);

        ItemDto updateAvailableFalse = ItemDto.builder()
                .available(false)
                .build();

        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(itemId);
        partialUpdatedItem.setName(existingItem.getName());
        partialUpdatedItem.setDescription(existingItem.getDescription());
        partialUpdatedItem.setAvailable(false);
        partialUpdatedItem.setUserId(ownerId);
        partialUpdatedItem.setRequestId(existingItem.getRequestId());

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name(existingItem.getName())
                .description(existingItem.getDescription())
                .available(false)
                .requestId(existingItem.getRequestId())
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(partialUpdatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(partialUpdatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, updateAvailableFalse);

        assertNotNull(result);
        assertFalse(result.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldUpdateItemWithRequestIdNullToNotNull() {
        existingItem.setRequestId(null);
        Long newRequestId = 50L;

        ItemDto updateWithRequest = ItemDto.builder()
                .requestId(newRequestId)
                .build();

        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(itemId);
        partialUpdatedItem.setName(existingItem.getName());
        partialUpdatedItem.setDescription(existingItem.getDescription());
        partialUpdatedItem.setAvailable(existingItem.getAvailable());
        partialUpdatedItem.setUserId(ownerId);
        partialUpdatedItem.setRequestId(newRequestId);

        ItemDto expectedResult = ItemDto.builder()
                .id(itemId)
                .name(existingItem.getName())
                .description(existingItem.getDescription())
                .available(existingItem.getAvailable())
                .requestId(newRequestId)
                .build();

        when(itemMapper.convertFromDto(any(ItemDto.class))).thenReturn(partialUpdatedItem);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(partialUpdatedItem);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(expectedResult);

        ItemDto result = itemService.updateItem(userId, itemId, updateWithRequest);

        assertNotNull(result);
        assertEquals(newRequestId, result.getRequestId());

        verify(itemRepository, times(1)).save(any(Item.class));
    }
}