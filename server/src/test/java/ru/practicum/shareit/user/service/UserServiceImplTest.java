package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserMapper userMapper;

    private User user;
    private UserDto userDto;
    private UserPatchDto userPatchDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        userPatchDto = new UserPatchDto();
        userPatchDto.setName("Updated User");
        userPatchDto.setEmail("updated@example.com");
    }

    @Test
    void shouldReturnUserDtoWhenUserExist() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.convertToDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).findById(userId);
        verify(userMapper).convertToDto(user);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldSaveUserWhenEmailIsUnique() {
        when(userMapper.convertFromDto(any(UserDto.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.convertToDto(user)).thenReturn(userDto);

        UserDto result = userService.addUser(userDto);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userMapper).convertFromDto(any(UserDto.class));
        verify(userRepository).save(any(User.class));
        verify(userMapper).convertToDto(user);
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailAlreadyExists() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Duplicate email"));

        assertThrows(RuntimeException.class, () -> userService.addUser(userDto));
    }

    @Test
    void shouldUpdateNameAndEmailWhenBothProvided() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@example.com");

        UserDto updatedDto = new UserDto();
        updatedDto.setId(userId);
        updatedDto.setName("Updated User");
        updatedDto.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.convertToDto(any(User.class))).thenReturn(updatedDto);

        UserDto result = userService.updateUser(userId, userPatchDto);

        assertNotNull(result);
        assertEquals("Updated User", result.getName());
        assertEquals("updated@example.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(userMapper).convertToDto(any(User.class));
    }

    @Test
    void shouldUpdateOnlyEmailWhenNameIsNull() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setEmail("new@example.com");

        UserDto updatedDto = new UserDto();
        updatedDto.setId(userId);
        updatedDto.setName("Old Name");
        updatedDto.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.convertToDto(any(User.class))).thenReturn(updatedDto);

        UserDto result = userService.updateUser(userId, patchDto);

        assertNotNull(result);
        assertEquals("Old Name", result.getName());
        assertEquals("new@example.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(userMapper).convertToDto(any(User.class));
    }

    @Test
    void shouldUpdateOnlyNameWhenEmailIsNull() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setName("New Name");

        UserDto updatedDto = new UserDto();
        updatedDto.setId(userId);
        updatedDto.setName("New Name");
        updatedDto.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.convertToDto(any(User.class))).thenReturn(updatedDto);

        UserDto result = userService.updateUser(userId, patchDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("old@example.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(userMapper).convertToDto(any(User.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistingUser() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userId, userPatchDto));

        assertEquals("Пользователь с id " + userId + " не найден", exception.getMessage());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.removeUser(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistingUser() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.removeUser(userId));

        assertEquals("Пользователь с id " + userId + " не найден", exception.getMessage());

        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(user);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.convertToDto(any(User.class))).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getName(), result.get(0).getName());

        verify(userRepository).findAll();
        verify(userMapper, times(1)).convertToDto(any(User.class));
    }
}