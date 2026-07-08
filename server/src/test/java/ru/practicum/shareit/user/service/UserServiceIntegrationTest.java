package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUserToDatabase() {
        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("Email@email");

        UserDto responseDto = userService.addUser(userDto);
        assertNotNull(responseDto.getId());
        assertEquals(userDto.getName(), responseDto.getName());
        assertEquals(userDto.getEmail(), responseDto.getEmail());

        User user = userRepository.findById(responseDto.getId()).orElseThrow();
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void shouldUpdateUserInDatabase() {
        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User userSaved = userRepository.save(user);

        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName("NewName");
        userPatchDto.setEmail("New@email");

        UserDto responseDto = userService.updateUser(userSaved.getId(), userPatchDto);

        assertEquals(userSaved.getId(), responseDto.getId());
        assertEquals(userPatchDto.getName(), responseDto.getName());
        assertEquals(userPatchDto.getEmail(), responseDto.getEmail());

        User userFromDb = userRepository.findById(userSaved.getId()).orElseThrow();
        assertEquals(userPatchDto.getName(), userFromDb.getName());
        assertEquals(userPatchDto.getEmail(), userFromDb.getEmail());
    }

    @Test
    void shouldUpdateOnlyNameInDatabase() {
        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User userSaved = userRepository.save(user);

        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName("NewName");

        UserDto responseDto = userService.updateUser(userSaved.getId(), userPatchDto);

        assertEquals(userSaved.getId(), responseDto.getId());
        assertEquals("NewName", responseDto.getName());
        assertEquals("Email@email", responseDto.getEmail());

        User userFromDb = userRepository.findById(userSaved.getId()).orElseThrow();
        assertEquals("NewName", userFromDb.getName());
        assertEquals("Email@email", userFromDb.getEmail());
    }

    @Test
    void shouldUpdateOnlyEmailInDatabase() {
        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User userSaved = userRepository.save(user);

        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setEmail("New@email");

        UserDto responseDto = userService.updateUser(userSaved.getId(), userPatchDto);

        assertEquals(userSaved.getId(), responseDto.getId());
        assertEquals("Name", responseDto.getName());
        assertEquals("New@email", responseDto.getEmail());

        User userFromDb = userRepository.findById(userSaved.getId()).orElseThrow();
        assertEquals("Name", userFromDb.getName());
        assertEquals("New@email", userFromDb.getEmail());
    }

    @Test
    void shouldDeleteUserFromDatabase() {
        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User userSaved = userRepository.save(user);

        userService.removeUser(userSaved.getId());

        Optional<User> userFromDb = userRepository.findById(userSaved.getId());
        assertTrue(userFromDb.isEmpty());
    }

    @Test
    void shouldFindUserDtoByIdFromDatabase() {
        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User userSaved = userRepository.save(user);

        UserDto userFromDb = userService.getUser(userSaved.getId());

        assertEquals(userSaved.getId(), userFromDb.getId());
        assertEquals(userSaved.getName(), userFromDb.getName());
        assertEquals(userSaved.getEmail(), userFromDb.getEmail());
    }

    @Test
    void shouldFindUserEntityByIdFromDatabase() {
        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User userSaved = userRepository.save(user);

        User userFromDb = userService.getUserById(userSaved.getId());

        assertEquals(userSaved.getId(), userFromDb.getId());
        assertEquals(userSaved.getName(), userFromDb.getName());
        assertEquals(userSaved.getEmail(), userFromDb.getEmail());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        Long nonExistentId = 999L;

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> userService.getUser(nonExistentId));

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> userService.getUserById(nonExistentId));

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> userService.removeUser(nonExistentId));

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> userService.updateUser(nonExistentId, new UserPatchDto()));
    }

    @Test
    void shouldReturnAllUsersFromDatabase() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@email.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@email.com");
        userRepository.save(user2);

        var users = userService.getAllUsers();

        assertNotNull(users);
        assertTrue(users.size() >= 2);

        boolean hasUser1 = users.stream().anyMatch(u -> "User1".equals(u.getName()));
        boolean hasUser2 = users.stream().anyMatch(u -> "User2".equals(u.getName()));
        assertTrue(hasUser1);
        assertTrue(hasUser2);
    }
}