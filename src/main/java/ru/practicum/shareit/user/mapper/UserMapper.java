package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.madel.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto convertToDto(User user);

    User convertFromDto(UserDto userDto);
}
