package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceDeleteItemTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Long ownerId;
    private Long nonOwnerId;
    private Long itemId;
    private User owner;
    private User nonOwner;
    private Item item;

    @BeforeEach
    void setUp() {
        ownerId = 1L;
        nonOwnerId = 3L;
        itemId = 2L;

        owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        nonOwner = new User();
        nonOwner.setId(nonOwnerId);
        nonOwner.setName("Non Owner");
        nonOwner.setEmail("nonowner@example.com");

        item = new Item();
        item.setId(itemId);
        item.setName("Drill");
        item.setDescription("Professional drill");
        item.setAvailable(true);
        item.setUserId(ownerId);
    }

    @Test
    void shouldDeleteItemWhenOwnerDeletes() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.removeItem(ownerId, itemId);

        verify(userService, times(1)).getUserById(ownerId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.removeItem(ownerId, itemId)
        );

        assertEquals("Вещь с id " + itemId + " не найдена", exception.getMessage());

        verify(userService, times(1)).getUserById(ownerId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userService.getUserById(ownerId)).thenThrow(new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.removeItem(ownerId, itemId)
        );

        assertEquals("Пользователь с id " + ownerId + " не найден", exception.getMessage());

        verify(userService, times(1)).getUserById(ownerId);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNonOwnerDeletes() {
        when(userService.getUserById(nonOwnerId)).thenReturn(nonOwner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.removeItem(nonOwnerId, itemId)
        );

        assertEquals(String.format("У пользователя с id %s не найдена вещь с id %s", nonOwnerId, itemId),
                exception.getMessage());

        verify(userService, times(1)).getUserById(nonOwnerId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldDeleteItemAndVerifyItemExists() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.removeItem(ownerId, itemId);

        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemIdDoesNotExist() {
        Long nonExistentItemId = 999L;
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(nonExistentItemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.removeItem(ownerId, nonExistentItemId)
        );

        assertEquals("Вещь с id " + nonExistentItemId + " не найдена", exception.getMessage());

        verify(userService, times(1)).getUserById(ownerId);
        verify(itemRepository, times(1)).findById(nonExistentItemId);
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotOwnItem() {
        Long anotherUserId = 5L;
        User anotherUser = new User();
        anotherUser.setId(anotherUserId);
        anotherUser.setName("Another User");

        Item anotherUserItem = new Item();
        anotherUserItem.setId(10L);
        anotherUserItem.setName("Another's Item");
        anotherUserItem.setUserId(anotherUserId);

        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(anotherUserItem));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.removeItem(ownerId, 10L)
        );

        assertEquals(String.format("У пользователя с id %s не найдена вещь с id %s", ownerId, 10L),
                exception.getMessage());

        verify(userService, times(1)).getUserById(ownerId);
        verify(itemRepository, times(1)).findById(10L);
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldDeleteItemAndNotThrowWhenOwnerMatches() {
        Long itemIdToDelete = 100L;
        Item itemToDelete = new Item();
        itemToDelete.setId(itemIdToDelete);
        itemToDelete.setName("Item to delete");
        itemToDelete.setUserId(ownerId);

        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(itemIdToDelete)).thenReturn(Optional.of(itemToDelete));

        itemService.removeItem(ownerId, itemIdToDelete);

        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void shouldCallGetUserByIdBeforeDeleting() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.removeItem(ownerId, itemId);

        InOrder inOrder = inOrder(userService, itemRepository);
        inOrder.verify(userService).getUserById(ownerId);
        inOrder.verify(itemRepository).findById(itemId);
        inOrder.verify(itemRepository).deleteById(anyLong());
    }

    @Test
    void shouldDeleteItemWithCorrectParameters() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.removeItem(ownerId, itemId);

        verify(itemRepository, atLeastOnce()).deleteById(anyLong());
    }
}