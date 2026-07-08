package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constant.Headers;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemDataForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private Long userId;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;
    private RequestDtoResponseWithMD requestDtoResponseWithMD;
    private List<RequestDtoResponseWithMD> requestDtoResponseList;

    @BeforeEach
    void setUp() {
        userId = 1L;

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");

        itemRequestDtoResponse = ItemRequestDtoResponse.builder()
                .id(1L)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .build();

        ItemDataForRequestDto itemData = ItemDataForRequestDto.builder()
                .id(1L)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .requestId(1L)
                .build();

        requestDtoResponseWithMD = RequestDtoResponseWithMD.builder()
                .id(1L)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .items(List.of(itemData))
                .build();

        requestDtoResponseList = List.of(requestDtoResponseWithMD);
    }

    @Test
    void shouldCreateNewRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), eq(userId)))
                .thenReturn(itemRequestDtoResponse);

        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoResponse.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoResponse.getDescription()));

        verify(itemRequestService, times(1))
                .createItemRequest(any(ItemRequestDto.class), eq(userId));
    }

    @Test
    void shouldReturn400WhenHeaderMissingForCreateRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    void shouldGetPrivateRequestsByUser() throws Exception {
        when(itemRequestService.getPrivateRequests(eq(userId), eq(0), eq(10)))
                .thenReturn(requestDtoResponseList);

        mockMvc.perform(get("/requests")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDtoResponseList.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(requestDtoResponseList.get(0).getDescription()));

        verify(itemRequestService, times(1))
                .getPrivateRequests(eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldGetPrivateRequestsByUserWithoutParams() throws Exception {
        when(itemRequestService.getPrivateRequests(eq(userId), eq(0), eq(10)))
                .thenReturn(requestDtoResponseList);

        mockMvc.perform(get("/requests")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDtoResponseList.get(0).getId()));

        verify(itemRequestService, times(1))
                .getPrivateRequests(eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldReturn400WhenHeaderMissingForGetPrivateRequests() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    void shouldGetOtherRequestsByUser() throws Exception {
        when(itemRequestService.getOtherRequests(eq(userId), eq(0), eq(10)))
                .thenReturn(requestDtoResponseList);

        mockMvc.perform(get("/requests/all")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDtoResponseList.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(requestDtoResponseList.get(0).getDescription()));

        verify(itemRequestService, times(1))
                .getOtherRequests(eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldGetOtherRequestsByUserWithoutParams() throws Exception {
        when(itemRequestService.getOtherRequests(eq(userId), eq(0), eq(10)))
                .thenReturn(requestDtoResponseList);

        mockMvc.perform(get("/requests/all")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDtoResponseList.get(0).getId()));

        verify(itemRequestService, times(1))
                .getOtherRequests(eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldReturn400WhenHeaderMissingForGetOtherRequests() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    void shouldGetRequestById() throws Exception {
        Long requestId = 1L;

        when(itemRequestService.getItemRequest(eq(userId), eq(requestId)))
                .thenReturn(requestDtoResponseWithMD);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDtoResponseWithMD.getId()))
                .andExpect(jsonPath("$.description").value(requestDtoResponseWithMD.getDescription()))
                .andExpect(jsonPath("$.items[0].id").value(requestDtoResponseWithMD.getItems().get(0).getId()));

        verify(itemRequestService, times(1))
                .getItemRequest(eq(userId), eq(requestId));
    }

    @Test
    void shouldReturn400WhenHeaderMissingForGetRequestById() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    void shouldReturn404WhenRequestNotFound() throws Exception {
        Long requestId = 999L;

        when(itemRequestService.getItemRequest(eq(userId), eq(requestId)))
                .thenThrow(new ru.practicum.shareit.exception.NotFoundException("Запроса с id = " + requestId + " нет"));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1))
                .getItemRequest(eq(userId), eq(requestId));
    }

    @Test
    void shouldReturn404WhenUserNotFoundForCreateRequest() throws Exception {
        Long nonExistentUserId = 999L;

        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), eq(nonExistentUserId)))
                .thenThrow(new ru.practicum.shareit.exception.NotFoundException("Пользователя с id = " + nonExistentUserId + " нет"));

        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID_HEADER, nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1))
                .createItemRequest(any(ItemRequestDto.class), eq(nonExistentUserId));
    }
}
