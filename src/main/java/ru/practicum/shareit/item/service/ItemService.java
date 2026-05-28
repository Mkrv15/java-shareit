package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.madel.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto getItem(Long id) {
        Item item = itemStorage.getItem(id);
        validateItemExists(item, id);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemStorage.getAllItems()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = ItemMapper.toItem(itemDto);
        User owner = userStorage.get(userId);
        newItem.setOwner(owner);

        Item createdItem = itemStorage.createItem(newItem);
        return ItemMapper.toItemDto(createdItem);
    }

    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        validateUserExists(userId);

        Item oldItem = itemStorage.getItem(itemId);
        validateItemExists(oldItem, itemId);
        validateItemOwner(oldItem, userId);

        Item sourceItem = ItemMapper.toItem(itemDto);
        ItemMapper.updateItemFields(sourceItem, oldItem);

        Item updatedItem = itemStorage.updateItem(oldItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    public void removeItem(Long id) {
        Item item = itemStorage.getItem(id);
        validateItemExists(item, id);
        itemStorage.removeItem(id);
    }

    public List<ItemDto> searchItemsByDescription(String text) {
        if (!text.isBlank()) {
            return itemStorage.getAllItems().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .map(ItemMapper::toItemDto)
                    .toList();
        }
        return Collections.emptyList();
    }

    private User validateUserExists(Long userId) {
        User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        return user;
    }

    private void validateItemExists(Item item, Long itemId) {
        if (item == null) {
            throw new NotFoundException(String.format("Вещь с id %d не найдена", itemId));
        }
    }

    private void validateItemOwner(Item item, Long userId) {
        if (item.getOwner() == null || !item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем данного товара!");
        }
    }
}
