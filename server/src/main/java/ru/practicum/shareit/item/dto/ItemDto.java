package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private long id;

    @NotBlank(message = "Поле с именем не должно быть пустым.")
    private String name;

    @NotBlank(message = "Поле с описанием не должно быть пустым.")
    private String description;

    @NotNull(message = "Поле Available не должно быть пустым.")
    private Boolean available;

    private Long requestId;

    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
    private List<CommentDto> comments;
}

