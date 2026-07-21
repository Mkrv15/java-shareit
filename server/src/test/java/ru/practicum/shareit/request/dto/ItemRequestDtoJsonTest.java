package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestDtoJsonTest {

    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDtoSerialization() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("Description")
                .build();

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Description");
    }

    @Test
    void testItemRequestDtoDeserialization() throws Exception {
        String content = "{\"description\":\"Description\"}";

        ItemRequestDto result = json.parseObject(content);

        assertThat(result.getDescription()).isEqualTo("Description");
    }
}