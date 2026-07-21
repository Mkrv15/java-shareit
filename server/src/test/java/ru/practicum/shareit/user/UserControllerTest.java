package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void shouldGetUserById() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(userService.getUser(userDto.getId())).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));

        verify(userService).getUser(userDto.getId());
    }

    @Test
    void shouldCreateUser() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(userService.addUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
        verify(userService).addUser(any(UserDto.class));
    }

    @Test
    void shouldReturn400WhenBodyMissingForCreate() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldPatchUser() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(userService.updateUser(eq(userDto.getId()), any(UserPatchDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));

        verify(userService).updateUser(eq(userDto.getId()), any(UserPatchDto.class));
    }

    @Test
    void shouldReturn400WhenBodyMissingForPatch() throws Exception {

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);

    }

    @Test
    void shouldDeleteUser() throws Exception {

        Long userId = 1L;

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService).removeUser(userId);
    }
}


