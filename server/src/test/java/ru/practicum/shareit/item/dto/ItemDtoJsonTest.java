package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> jsonItemDto;

    @Test
    public void testItemDtoSerialization() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("Name")
                .description("Description")
                .available(true)
                .build();

        JsonContent<ItemDto> result = jsonItemDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("@.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("@.available").isTrue();
    }

    @Test
    public void testItemDtoDeserialization() throws Exception {
        String content = "{\"id\":1,\"name\":\"Name\",\"description\":\"Description\",\"available\":true}";

        ItemDto result = jsonItemDto.parseObject(content);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Name");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getAvailable()).isTrue();
    }
}
