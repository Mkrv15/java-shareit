package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getAllItems(Long userId);

    List<ItemDto> searchItems(String text);

    void removeItem(Long userId, Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
