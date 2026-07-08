package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.constant.Headers;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private Long userId;
    private Long bookingId;
    private Long itemId;
    private LocalDateTime now;
    private BookItemRequestDto bookItemRequestDto;

    @BeforeEach
    void setUp() {
        userId = 15L;
        bookingId = 1L;
        itemId = 15L;
        now = LocalDateTime.now();

        bookItemRequestDto = new BookItemRequestDto(
                itemId,
                now.plusMinutes(1),
                now.plusMinutes(30)
        );
    }

    @Test
    void shouldCreateBookingSuccessfully() throws Exception {
        when(bookingClient.bookItem(eq(userId), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).bookItem(eq(userId), any(BookItemRequestDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenStartIsNull() throws Exception {
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                itemId,
                null,
                now.plusMinutes(30)
        );

        mockMvc.perform(post("/bookings")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenEndIsNull() throws Exception {
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                itemId,
                now.plusMinutes(1),
                null
        );

        mockMvc.perform(post("/bookings")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWithoutHeaderForCreate() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenStartInPast() throws Exception {
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                itemId,
                now.minusMinutes(1),
                now.plusMinutes(30)
        );

        mockMvc.perform(post("/bookings")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenItemIdIsZero() throws Exception {
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                0L,
                now.plusMinutes(1),
                now.plusMinutes(30)
        );

        mockMvc.perform(post("/bookings")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldGetBookingByIdSuccessfully() throws Exception {
        when(bookingClient.getBooking(eq(userId), eq(bookingId)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(eq(userId), eq(bookingId));
    }

    @Test
    void shouldReturnBadRequestWhenIncorrectPathForGetById() throws Exception {
        mockMvc.perform(get("/bookings/incorrect")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWithoutHeaderForGetById() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldGetBookingsFromOwnerSuccessfully() throws Exception {
        when(bookingClient.getBookingsFromOwner(eq(userId), eq(BookingState.WAITING), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("state", "WAITING")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .getBookingsFromOwner(eq(userId), eq(BookingState.WAITING), eq(1), eq(10));
    }

    @Test
    void shouldGetBookingsFromOwnerSuccessfullyWithoutParams() throws Exception {
        when(bookingClient.getBookingsFromOwner(eq(userId), eq(BookingState.ALL), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .getBookingsFromOwner(eq(userId), eq(BookingState.ALL), eq(0), eq(10));
    }

    @Test
    void shouldReturnBadRequestWithoutHeaderForOwner() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenNegativeFromForOwner() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenZeroSizeForOwner() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenIncorrectStateForOwner() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("state", "incorrect")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldGetBookingsFromBookerSuccessfully() throws Exception {
        when(bookingClient.getBookingsFromBooker(eq(userId), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .getBookingsFromBooker(eq(userId), eq(BookingState.ALL), eq(1), eq(10));
    }

    @Test
    void shouldGetBookingsFromBookerSuccessfullyWithoutParams() throws Exception {
        when(bookingClient.getBookingsFromBooker(eq(userId), eq(BookingState.ALL), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .getBookingsFromBooker(eq(userId), eq(BookingState.ALL), eq(0), eq(10));
    }

    @Test
    void shouldReturnBadRequestWithoutHeaderForBooker() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenNegativeFromForBooker() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenZeroSizeForBooker() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenIncorrectStateForBooker() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("state", "incorrect")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldApproveBookingSuccessfully() throws Exception {
        when(bookingClient.updateBookingById(eq(userId), eq(bookingId), eq(true)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .updateBookingById(eq(userId), eq(bookingId), eq(true));
    }

    @Test
    void shouldRejectBookingSuccessfully() throws Exception {
        when(bookingClient.updateBookingById(eq(userId), eq(bookingId), eq(false)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("approved", "false"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .updateBookingById(eq(userId), eq(bookingId), eq(false));
    }

    @Test
    void shouldReturnBadRequestWithoutApprovedParam() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWithoutHeaderForPatch() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenIncorrectPathForPatch() throws Exception {
        mockMvc.perform(patch("/bookings/incorrect")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void shouldReturnBadRequestWhenApprovedParamIsInvalid() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("approved", "invalid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }
}