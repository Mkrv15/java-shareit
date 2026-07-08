package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private long id;
    @NotBlank(message = "Комментарий не должен быть пустым.")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
