package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.madel.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto get(Long id) {
        return UserMapper.toUserDto(userStorage.get(id));
    }

    public List<UserDto> getAll() {
        return userStorage.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto add(UserDto userDto) {
        User user = userStorage.add(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public UserDto update(UserDto userDto, Long id) {
        userDto.setId(id);
        return UserMapper.toUserDto(userStorage.update(UserMapper.toUser(userDto)));
    }

    public Boolean delete(Long id) {
        return userStorage.delete(id);
    }
}
