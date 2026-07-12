package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDto> jsonBookingDto;

    @Test
    void testBookingDtoSerialization() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .end(LocalDateTime.of(2024, 1, 2, 10, 0, 0))
                .status(Status.WAITING)
                .build();

        var result = jsonBookingDto.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-01-02T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("WAITING");
    }

    @Test
    void testBookingDtoDeserialization() throws Exception {
        String content = "{\"id\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\",\"status\":\"WAITING\"}";

        BookingDto result = jsonBookingDto.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        assertThat(result.getEnd()).isEqualTo(
                LocalDateTime.of(2024, 1, 2, 10, 0, 0));
        assertThat(result.getStatus()).isEqualTo(Status.WAITING);
    }
}
