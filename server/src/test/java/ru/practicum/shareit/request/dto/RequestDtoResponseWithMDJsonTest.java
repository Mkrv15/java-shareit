package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RequestDtoResponseWithMDJsonTest {

    private final JacksonTester<RequestDtoResponseWithMD> json;

    @Test
    void testRequestDtoResponseWithMDSerialization() throws Exception {
        List<ItemDataForRequestDto> items = Arrays.asList(
                ItemDataForRequestDto.builder()
                        .id(1L)
                        .name("Item 1")
                        .description("Description 1")
                        .available(true)
                        .requestId(1L)
                        .ownerId(1L)
                        .build(),
                ItemDataForRequestDto.builder()
                        .id(2L)
                        .name("Item 2")
                        .description("Description 2")
                        .available(false)
                        .requestId(1L)
                        .ownerId(1L)
                        .build()
        );

        RequestDtoResponseWithMD dto = RequestDtoResponseWithMD.builder()
                .id(1L)
                .description("Test Request")
                .created(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .items(items)
                .ownerId(1L)
                .build();

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Test Request");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2024-01-01T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Item 1");
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
    }

    @Test
    void testRequestDtoResponseWithMDDeserialization() throws Exception {
        String content = "{\"id\":1,\"description\":\"Test Request\",\"created\":\"" +
                "2024-01-01T10:00:00\",\"ownerId\":1,\"items\":[]}";

        RequestDtoResponseWithMD result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Test Request");
        assertThat(result.getCreated()).isEqualTo(
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        assertThat(result.getOwnerId()).isEqualTo(1L);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void testRequestDtoResponseWithMDWithoutItems() throws Exception {
        RequestDtoResponseWithMD dto = RequestDtoResponseWithMD.builder()
                .id(1L)
                .description("Test Request")
                .created(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .ownerId(1L)
                .build();

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Test Request");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).doesNotHaveJsonPathValue("$.items");
    }
}
