package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.madel.User;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    User getUserById(Long userId);

    UserDto getUser(Long userId);

    List<UserDto> getAllUsers();

    void removeUser(Long userId);
}
