package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private Long userId;
    private Long bookingId;
    private LocalDateTime now;
    private BookingDto bookingDto;
    private BookingInputDto bookingInputDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        bookingId = 15L;
        now = LocalDateTime.now();

        bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStart(now.plusMinutes(10));
        bookingDto.setEnd(now.plusMinutes(20));

        bookingInputDto = new BookingInputDto();
        bookingInputDto.setItemId(2L);
        bookingInputDto.setStart(now.plusMinutes(10));
        bookingInputDto.setEnd(now.plusMinutes(20));
    }

    @Test
    void shouldGetBooking() throws Exception {
        when(bookingService.getBooking(eq(bookingId), eq(userId), eq(AccessLevel.OWNER_AND_BOOKER)))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1))
                .getBooking(eq(bookingId), eq(userId), eq(AccessLevel.OWNER_AND_BOOKER));
    }

    @Test
    void shouldGetBookingsByStateToBooker() throws Exception {
        BookingDto bookingDtoFirst = new BookingDto();
        bookingDtoFirst.setId(15L);

        BookingDto bookingDtoSecond = new BookingDto();
        bookingDtoSecond.setId(16L);

        List<BookingDto> bookingDtoList = List.of(bookingDtoFirst, bookingDtoSecond);

        when(bookingService.getBookingsOfCurrentUser(eq(State.ALL), eq(userId), eq(0), eq(10)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(bookingDtoSecond.getId()));

        verify(bookingService, times(1))
                .getBookingsOfCurrentUser(eq(State.ALL), eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldGetBookingsByStateToBookerWithDefaultParams() throws Exception {
        BookingDto bookingDtoFirst = new BookingDto();
        bookingDtoFirst.setId(15L);

        List<BookingDto> bookingDtoList = List.of(bookingDtoFirst);

        when(bookingService.getBookingsOfCurrentUser(eq(State.ALL), eq(userId), eq(0), eq(10)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1))
                .getBookingsOfCurrentUser(eq(State.ALL), eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldGetBookingsByStateToOwner() throws Exception {
        BookingDto bookingDtoFirst = new BookingDto();
        bookingDtoFirst.setId(15L);

        BookingDto bookingDtoSecond = new BookingDto();
        bookingDtoSecond.setId(16L);

        List<BookingDto> bookingDtoList = List.of(bookingDtoFirst, bookingDtoSecond);

        when(bookingService.getBookingsOfOwner(eq(State.ALL), eq(userId), eq(0), eq(10)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(bookingDtoSecond.getId()));

        verify(bookingService, times(1))
                .getBookingsOfOwner(eq(State.ALL), eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldGetBookingsByStateToOwnerWithoutFromAndSize() throws Exception {
        BookingDto bookingDtoFirst = new BookingDto();
        bookingDtoFirst.setId(15L);

        BookingDto bookingDtoSecond = new BookingDto();
        bookingDtoSecond.setId(16L);

        List<BookingDto> bookingDtoList = List.of(bookingDtoFirst, bookingDtoSecond);

        when(bookingService.getBookingsOfOwner(eq(State.ALL), eq(userId), eq(0), eq(10)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(bookingDtoSecond.getId()));

        verify(bookingService, times(1))
                .getBookingsOfOwner(eq(State.ALL), eq(userId), eq(0), eq(10));
    }

    @Test
    void shouldCreateBooking() throws Exception {
        when(bookingService.addBooking(eq(userId), any(BookingInputDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1))
                .addBooking(eq(userId), any(BookingInputDto.class));
    }

    @Test
    void shouldApproveBooking() throws Exception {
        boolean approved = true;
        when(bookingService.approveOrRejectBooking(eq(userId), eq(bookingId), eq(approved), eq(AccessLevel.OWNER)))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1))
                .approveOrRejectBooking(eq(userId), eq(bookingId), eq(approved), eq(AccessLevel.OWNER));
    }

    @Test
    void shouldRejectBooking() throws Exception {
        boolean approved = false;
        when(bookingService.approveOrRejectBooking(eq(userId), eq(bookingId), eq(approved), eq(AccessLevel.OWNER)))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1))
                .approveOrRejectBooking(eq(userId), eq(bookingId), eq(approved), eq(AccessLevel.OWNER));
    }

    @Test
    void should400BadRequestWithoutHeaderToGetMapping() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    void should400BadRequestWithoutHeaderToPostMapping() throws Exception {
        mockMvc.perform(post("/bookings"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    void should400BadRequestWithoutHeaderToPatchMapping() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    void should400BadRequestWithoutState() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1))
                .getBookingsOfCurrentUser(eq(State.ALL), eq(userId), eq(0), eq(10));
    }

    @Test
    void should400BadRequestIncorrectState() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "INCORRECT"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    void should400BadRequestWithoutApproved() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }
}