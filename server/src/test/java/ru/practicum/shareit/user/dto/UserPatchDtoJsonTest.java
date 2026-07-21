package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserPatchDtoJsonTest {

    @Autowired
    private JacksonTester<UserPatchDto> json;

    @Test
    void testUserPatchDtoSerialization() throws Exception {
        UserPatchDto dto = UserPatchDto.builder()
                .name("Updated Name")
                .email("updated@mail.com")
                .build();

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Updated Name");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("updated@mail.com");
    }

    @Test
    void testUserPatchDtoDeserialization() throws Exception {
        String content = "{\"name\":\"Updated Name\",\"email\":\"updated@mail.com\"}";

        UserPatchDto result = json.parseObject(content);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@mail.com");
    }

    @Test
    void testUserPatchDtoEmpty() throws Exception {
        UserPatchDto dto = UserPatchDto.builder().build();

        var result = json.write(dto);

        assertThat(result).doesNotHaveJsonPathValue("$.name");
        assertThat(result).doesNotHaveJsonPathValue("$.email");
    }
}