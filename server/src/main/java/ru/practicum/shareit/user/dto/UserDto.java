package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    @Email(message = "Поле email заполненно некорректно. Проверьте формат.")
    @NotBlank(message = "Поле email не должно быть пустым.")
    private String email;
    @NotBlank(message = "Поле name не должно быть пустым.")
    private String name;
}
