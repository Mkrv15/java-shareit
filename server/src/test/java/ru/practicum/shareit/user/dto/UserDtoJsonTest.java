package ru.practicum.shareit.user.dto;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDtoJsonTest {

    private final JacksonTester<UserDto> jsonUserDto;

    @Test
    void testUserDtoSerialization() throws Exception {
        UserDto userDto = new UserDto().builder()
                .id(1)
                .name("Name")
                .email("email@email.ru")
                .build();

        JsonContent<UserDto> result = jsonUserDto.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("@.email").isEqualTo("email@email.ru");
    }

    @Test
    void testUserDtoDeserialization() throws Exception {
        String content = "{\"id\":1,\"name\":\"Name\",\"email\":\"email@email.ru\"}";
        UserDto userDto = jsonUserDto.parseObject(content);

        assertThat(userDto.getId()).isEqualTo(1);
        assertThat(userDto.getName()).isEqualTo("Name");
        assertThat(userDto.getEmail()).isEqualTo("email@email.ru");
    }
}
