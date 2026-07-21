package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingInputDtoJsonTest {

    private final JacksonTester<BookingInputDto> json;

    @Test
    void testBookingInputDtoSerialization() throws Exception {
        BookingInputDto dto = new BookingInputDto();
        dto.setId(1L);
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2024, 1, 2, 10, 0, 0));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-02T10:00:00");
    }

    @Test
    void testBookingInputDtoDeserialization() throws Exception {
        String content = "{\"id\":1,\"itemId\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\"}";

        BookingInputDto result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        assertThat(result.getEnd()).isEqualTo(
                LocalDateTime.of(2024, 1, 2, 10, 0, 0));
    }
}
