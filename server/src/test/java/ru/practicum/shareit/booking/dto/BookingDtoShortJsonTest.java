package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingDtoShortJsonTest {

    private final JacksonTester<BookingDtoShort> json;

    @Test
    void testBookingDtoShortSerialization() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        BookingDtoShort dto = new BookingDtoShort();
        dto.setId(1L);
        dto.setItem(itemDto);
        dto.setBookerId(1L);
        dto.setStart(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2024, 1, 2, 10, 0, 0));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-02T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
    }

    @Test
    void testBookingDtoShortDeserialization() throws Exception {
        String content = "{\"id\":1,\"bookerId\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\"}";

        BookingDtoShort result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBookerId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0, 0));
    }
}
