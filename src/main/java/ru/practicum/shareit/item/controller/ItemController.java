package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(userIdHeader) Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        return itemService.searchItemsByDescription(text);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(userIdHeader) Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader(userIdHeader) Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        itemService.removeItem(itemId);
    }
}
