package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constant.Headers;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    private Long userId;
    private ItemRequestInputDto itemRequestInputDto;

    @BeforeEach
    void setUp() {
        userId = 15L;
        itemRequestInputDto = new ItemRequestInputDto("Need a drill");
    }

    @Test
    void shouldCreateRequestSuccessfully() throws Exception {
        when(requestClient.createRequest(eq(userId), any(ItemRequestInputDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestInputDto)))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).createRequest(eq(userId),
                any(ItemRequestInputDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionBlank() throws Exception {
        ItemRequestInputDto emptyRequest = new ItemRequestInputDto(" ");

        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionNull() throws Exception {
        ItemRequestInputDto nullRequest = new ItemRequestInputDto(null);

        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWithoutHeader() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestInputDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }


    @Test
    void shouldGetRequestsSuccessfully() throws Exception {
        when(requestClient.getRequests(eq(userId), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getRequests(eq(userId), eq(1), eq(10));
    }

    @Test
    void shouldGetRequestsSuccessfullyWithoutParams() throws Exception {
        when(requestClient.getRequests(eq(userId), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getRequests(eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldReturnBadRequestWithoutHeaderForGetRequests() throws Exception {
        mockMvc.perform(get("/requests")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWhenNegativeFromForGetRequests() throws Exception {
        mockMvc.perform(get("/requests")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWhenZeroSizeForGetRequests() throws Exception {
        mockMvc.perform(get("/requests")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWhenNegativeSizeForGetRequests() throws Exception {
        mockMvc.perform(get("/requests")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }


    @Test
    void shouldGetAllRequestsSuccessfully() throws Exception {
        when(requestClient.getAllRequests(eq(userId), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getAllRequests(eq(userId), eq(1), eq(10));
    }

    @Test
    void shouldGetAllRequestsSuccessfullyWithoutParams() throws Exception {
        when(requestClient.getAllRequests(eq(userId), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getAllRequests(eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldReturnBadRequestWithoutHeaderForGetAllRequests() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWhenNegativeFromForGetAllRequests() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWhenZeroSizeForGetAllRequests() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWhenNegativeSizeForGetAllRequests() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }


    @Test
    void shouldGetRequestByIdSuccessfully() throws Exception {
        Long requestId = 1L;
        when(requestClient.getRequest(eq(userId), eq(requestId)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getRequest(eq(userId), eq(requestId));
    }


    @Test
    void shouldReturnBadRequestWithoutHeaderForGetById() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWhenRequestIdIsNegative() throws Exception {
        mockMvc.perform(get("/requests/-1")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void shouldReturnBadRequestWhenRequestIdIsZero() throws Exception {
        mockMvc.perform(get("/requests/0")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }
}