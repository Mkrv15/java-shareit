package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestDtoResponseJsonTest {

    private final JacksonTester<ItemRequestDtoResponse> json;

    @Test
    void testItemRequestDtoResponseSerialization() throws Exception {
        ItemRequestDtoResponse dto = ItemRequestDtoResponse.builder()
                .id(1L)
                .description("Test Request")
                .created(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .build();

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Test Request");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2024-01-01T10:00:00");
    }

    @Test
    void testItemRequestDtoResponseDeserialization() throws Exception {
        String content = "{\"id\":1,\"description\":\"Test Request\",\"created\":\"2024-01-01T10:00:00\"}";

        ItemRequestDtoResponse result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Test Request");
        assertThat(result.getCreated()).isEqualTo(
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
    }
}
