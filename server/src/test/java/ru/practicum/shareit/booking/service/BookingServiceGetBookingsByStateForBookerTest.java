package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.strategy.BookingFetchStrategy;
import ru.practicum.shareit.booking.strategy.BookingStrategyFactory;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceGetBookingsByStateForBookerTest {

    @Mock
    private UserService userService;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingStrategyFactory bookerStrategyFactory;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Long bookerId;
    private Long itemId;
    private Long ownerId;
    private int from;
    private int size;
    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private List<Booking> bookings;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        bookerId = 1L;
        itemId = 2L;
        ownerId = 3L;
        from = 0;
        size = 10;

        booker = new User();
        booker.setId(bookerId);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        item = new Item();
        item.setId(itemId);
        item.setName("Drill");
        item.setDescription("Professional drill");
        item.setAvailable(true);
        item.setUserId(ownerId);

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setStatus(Status.WAITING);

        bookings = new ArrayList<>();
        bookings.add(booking);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(Status.WAITING);

        pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsAll() {
        State state = State.ALL;
        BookingFetchStrategy strategy = mock(BookingFetchStrategy.class);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookerStrategyFactory.getStrategy(state)).thenReturn(strategy);
        when(strategy.findBookings(bookerId, pageable)).thenReturn(bookings);
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(state, bookerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookerStrategyFactory, times(1)).getStrategy(state);
        verify(strategy, times(1)).findBookings(bookerId, pageable);
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsPast() {
        State state = State.PAST;
        BookingFetchStrategy strategy = mock(BookingFetchStrategy.class);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookerStrategyFactory.getStrategy(state)).thenReturn(strategy);
        when(strategy.findBookings(bookerId, pageable)).thenReturn(bookings);
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(state, bookerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookerStrategyFactory, times(1)).getStrategy(state);
        verify(strategy, times(1)).findBookings(bookerId, pageable);
    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsCurrent() {
        State state = State.CURRENT;
        BookingFetchStrategy strategy = mock(BookingFetchStrategy.class);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookerStrategyFactory.getStrategy(state)).thenReturn(strategy);
        when(strategy.findBookings(bookerId, pageable)).thenReturn(bookings);
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(state, bookerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookerStrategyFactory, times(1)).getStrategy(state);
        verify(strategy, times(1)).findBookings(bookerId, pageable);
    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsFuture() {
        State state = State.FUTURE;
        BookingFetchStrategy strategy = mock(BookingFetchStrategy.class);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookerStrategyFactory.getStrategy(state)).thenReturn(strategy);
        when(strategy.findBookings(bookerId, pageable)).thenReturn(bookings);
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(state, bookerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookerStrategyFactory, times(1)).getStrategy(state);
        verify(strategy, times(1)).findBookings(bookerId, pageable);
    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsWaiting() {
        State state = State.WAITING;
        BookingFetchStrategy strategy = mock(BookingFetchStrategy.class);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookerStrategyFactory.getStrategy(state)).thenReturn(strategy);
        when(strategy.findBookings(bookerId, pageable)).thenReturn(bookings);
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(state, bookerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookerStrategyFactory, times(1)).getStrategy(state);
        verify(strategy, times(1)).findBookings(bookerId, pageable);
    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsRejected() {
        State state = State.REJECTED;
        BookingFetchStrategy strategy = mock(BookingFetchStrategy.class);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookerStrategyFactory.getStrategy(state)).thenReturn(strategy);
        when(strategy.findBookings(bookerId, pageable)).thenReturn(bookings);
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(state, bookerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookerStrategyFactory, times(1)).getStrategy(state);
        verify(strategy, times(1)).findBookings(bookerId, pageable);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookerNotFound() {
        State state = State.ALL;

        when(userService.getUserById(bookerId)).thenThrow(new NotFoundException("Пользователь с id " + bookerId + " не найден"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingsOfCurrentUser(state, bookerId, from, size)
        );

        assertEquals("Пользователь с id " + bookerId + " не найден", exception.getMessage());

        verify(userService, times(1)).getUserById(bookerId);
        verifyNoInteractions(bookerStrategyFactory);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void shouldReturnEmptyListWhenNoBookings() {
        State state = State.ALL;
        BookingFetchStrategy strategy = mock(BookingFetchStrategy.class);
        List<Booking> emptyList = new ArrayList<>();

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookerStrategyFactory.getStrategy(state)).thenReturn(strategy);
        when(strategy.findBookings(bookerId, pageable)).thenReturn(emptyList);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(state, bookerId, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookerStrategyFactory, times(1)).getStrategy(state);
        verify(strategy, times(1)).findBookings(bookerId, pageable);
        verify(bookingMapper, never()).convertToDto(any(Booking.class));
    }

    @Test
    void shouldHandlePaginationCorrectly() {
        State state = State.ALL;
        BookingFetchStrategy strategy = mock(BookingFetchStrategy.class);
        int customFrom = 5;
        int customSize = 10;
        Pageable customPageable = PageRequest.of(customFrom / customSize, customSize, Sort.by(Sort.Direction.DESC, "start"));

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookerStrategyFactory.getStrategy(state)).thenReturn(strategy);
        when(strategy.findBookings(bookerId, customPageable)).thenReturn(bookings);
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(state, bookerId, customFrom, customSize);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(strategy, times(1)).findBookings(bookerId, customPageable);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenStateNotRegistered() {
        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookerStrategyFactory.getStrategy(any(State.class)))
                .thenThrow(new IllegalArgumentException("Booking strategy for state ALL is not registered"));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsOfCurrentUser(State.ALL, bookerId, from, size));

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookerStrategyFactory, times(1)).getStrategy(any(State.class));
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(bookingMapper);
    }
}