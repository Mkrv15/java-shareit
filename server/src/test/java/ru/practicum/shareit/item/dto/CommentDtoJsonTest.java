package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDtoSerialization() throws Exception {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .authorName("John Doe")
                .created(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .build();

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Test comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:00:00");
    }

    @Test
    void testCommentDtoDeserialization() throws Exception {
        String content = "{\"id\":1,\"text\":\"Test comment\",\"authorName\":\"John Doe\"" +
                ",\"created\":\"2024-01-01T10:00:00\"}";

        CommentDto result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Test comment");
        assertThat(result.getAuthorName()).isEqualTo("John Doe");
        assertThat(result.getCreated()).isEqualTo(
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
    }
}
