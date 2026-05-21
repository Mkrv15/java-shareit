package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.madel.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
