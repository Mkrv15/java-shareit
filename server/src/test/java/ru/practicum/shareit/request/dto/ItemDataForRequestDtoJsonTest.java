package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemDataForRequestDtoJsonTest {

    private final JacksonTester<ItemDataForRequestDto> json;

    @Test
    void testItemDataForRequestDtoSerialization() throws Exception {
        ItemDataForRequestDto dto = ItemDataForRequestDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(1L)
                .ownerId(1L)
                .build();

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test Item");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Test Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
    }

    @Test
    void testItemDataForRequestDtoDeserialization() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\"" +
                ",\"available\":true,\"requestId\":1,\"ownerId\":1}";

        ItemDataForRequestDto result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(1L);
        assertThat(result.getOwnerId()).isEqualTo(1L);
    }

    @Test
    void testItemDataForRequestDtoWithNullFields() throws Exception {
        ItemDataForRequestDto dto = ItemDataForRequestDto.builder()
                .id(1L)
                .name("Test Item")
                .build();

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test Item");
        assertThat(result).doesNotHaveJsonPathValue("$.description");
        assertThat(result).doesNotHaveJsonPathValue("$.available");
        assertThat(result).doesNotHaveJsonPathValue("$.requestId");
        assertThat(result).doesNotHaveJsonPathValue("$.ownerId");
    }
}