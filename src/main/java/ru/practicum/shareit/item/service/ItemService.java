package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
        itemIdValidator(itemStorage.getItem(id));
        return ItemMapper.toItemDto(itemStorage.getItem(id));
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
        itemOwnerCheckValidator(owner, newItem, userId);
        Item createdItem = itemStorage.createItem(newItem);
        return ItemMapper.toItemDto(createdItem);
    }

    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        userIdValidator(userId);
        Item oldItem = itemStorage.getItem(itemId);
        itemOwnerNameDescAvailValidator(item, oldItem, userId);
        Item changedItem = itemStorage.updateItem(oldItem);
        return ItemMapper.toItemDto(changedItem);
    }

    public void removeItem(Long id) {
        itemIdValidator(itemStorage.getItem(id));
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

    private void itemIdValidator(Item item) {
        if (!itemStorage.getAllItems().contains(itemStorage.getItem(item.getId()))) {
            throw new NotFoundException("Вещь с id " + item.getId() + " не найдена");
        }
        if (item.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (item.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
    }

    private void itemOwnerCheckValidator(User owner, Item newItem, long id) {
        if (owner == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", id));
        } else {
            newItem.setOwner(owner);
        }
    }

    private void itemOwnerNameDescAvailValidator(Item item, Item oldItem, long userId) {
        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("Пользователь не является владельцем данного товара!");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
    }

    private void userIdValidator(Long userId) {
        if (!userStorage.getAll().contains(userStorage.get(userId))) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
    }
}
