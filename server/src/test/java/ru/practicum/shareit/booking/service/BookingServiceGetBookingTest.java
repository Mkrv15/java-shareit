package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceGetBookingTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Long bookerId;
    private Long itemId;
    private Long ownerId;
    private Long bookingId;
    private Long nonOwnerNonBookerId;
    private User booker;
    private User owner;
    private User nonOwnerNonBooker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookerId = 1L;
        itemId = 2L;
        ownerId = 3L;
        bookingId = 4L;
        nonOwnerNonBookerId = 5L;

        booker = new User();
        booker.setId(bookerId);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        nonOwnerNonBooker = new User();
        nonOwnerNonBooker.setId(nonOwnerNonBookerId);
        nonOwnerNonBooker.setName("Other User");
        nonOwnerNonBooker.setEmail("other@example.com");

        item = new Item();
        item.setId(itemId);
        item.setName("Drill");
        item.setDescription("Professional drill");
        item.setAvailable(true);
        item.setUserId(ownerId);

        booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setStatus(Status.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStatus(Status.WAITING);
    }

    @Test
    void shouldReturnBookingWhenRequestedByOwner() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.getBooking(bookingId, ownerId, AccessLevel.OWNER_AND_BOOKER);

        assertNotNull(result);

        verify(userService, times(1)).getUserById(ownerId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldReturnBookingWhenRequestedByBooker() {
        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.getBooking(bookingId, bookerId, AccessLevel.OWNER_AND_BOOKER);

        assertNotNull(result);

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookingDoesNotExist() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(bookingId, ownerId, AccessLevel.OWNER_AND_BOOKER)
        );

        assertEquals("Бронирование с id " + bookingId + " не найдено", exception.getMessage());

        verify(userService, times(1)).getUserById(ownerId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userService.getUserById(ownerId)).thenThrow(new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(bookingId, ownerId, AccessLevel.OWNER_AND_BOOKER)
        );

        assertEquals("Пользователь с id " + ownerId + " не найден", exception.getMessage());

        verify(userService, times(1)).getUserById(ownerId);
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void shouldThrowAccessExceptionWhenUserIsNotOwnerOrBooker() {
        when(userService.getUserById(nonOwnerNonBookerId)).thenReturn(nonOwnerNonBooker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        AccessException exception = assertThrows(
                AccessException.class,
                () -> bookingService.getBooking(bookingId, nonOwnerNonBookerId, AccessLevel.OWNER_AND_BOOKER)
        );

        assertEquals("У пользователя с id " + nonOwnerNonBookerId + " нет прав на просмотр бронирования с id " + bookingId + ",",
                exception.getMessage());

        verify(userService, times(1)).getUserById(nonOwnerNonBookerId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
        verify(bookingMapper, never()).convertToDto(any(Booking.class));
    }

    @Test
    void shouldThrowAccessExceptionWhenOwnerAccessAndUserIsNotOwner() {
        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        AccessException exception = assertThrows(
                AccessException.class,
                () -> bookingService.getBooking(bookingId, bookerId, AccessLevel.OWNER)
        );

        assertEquals("У пользователя с id " + bookerId + " нет прав на просмотр бронирования с id " + bookingId + ",",
                exception.getMessage());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
        verify(bookingMapper, never()).convertToDto(any(Booking.class));
    }

    @Test
    void shouldThrowAccessExceptionWhenBookerAccessAndUserIsNotBooker() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        AccessException exception = assertThrows(
                AccessException.class,
                () -> bookingService.getBooking(bookingId, ownerId, AccessLevel.BOOKER)
        );

        assertEquals("У пользователя с id " + ownerId + " нет прав на просмотр бронирования с id " + bookingId + ",",
                exception.getMessage());

        verify(userService, times(1)).getUserById(ownerId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
        verify(bookingMapper, never()).convertToDto(any(Booking.class));
    }

    @Test
    void shouldUseCorrectAccessLevelForOwnerAndBooker() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.getBooking(bookingId, ownerId, AccessLevel.OWNER_AND_BOOKER);

        assertNotNull(result);

        verify(userService, times(1)).getUserById(ownerId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
    }

    @Test
    void shouldReturnBookingWithCorrectData() {
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.getBooking(bookingId, ownerId, AccessLevel.OWNER_AND_BOOKER);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(Status.WAITING, result.getStatus());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
    }
}
