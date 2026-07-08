package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ItemNotAvailableForBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceAddBookingTest {

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
    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingInputDto bookingInputDto;
    private BookingDto bookingDto;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        bookerId = 1L;
        itemId = 2L;
        ownerId = 3L;

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

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(3);

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(Status.WAITING);

        bookingInputDto = new BookingInputDto();
        bookingInputDto.setItemId(itemId);
        bookingInputDto.setStart(start);
        bookingInputDto.setEnd(end);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(Status.WAITING);
    }

    @Test
    void shouldCreateBookingWhenValidData() {
        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.convertFromDto(any(BookingInputDto.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.addBooking(bookerId, bookingInputDto);

        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());

        verify(userService, times(1)).getUserById(bookerId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingMapper, times(1)).convertFromDto(any(BookingInputDto.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(bookingMapper.convertFromDto(any(BookingInputDto.class))).thenReturn(booking);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookerId, bookingInputDto)
        );

        assertEquals("Вещь с id " + itemId + " не найдена", exception.getMessage());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookingMapper, times(1)).convertFromDto(any(BookingInputDto.class));
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userService.getUserById(bookerId)).thenThrow(new NotFoundException("Пользователь с id " + bookerId + " не найден"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookerId, bookingInputDto)
        );

        assertEquals("Пользователь с id " + bookerId + " не найден", exception.getMessage());

        verify(userService, times(1)).getUserById(bookerId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowItemNotAvailableForBookingExceptionWhenItemNotAvailable() {
        item.setAvailable(false);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.convertFromDto(any(BookingInputDto.class))).thenReturn(booking);

        ItemNotAvailableForBookingException exception = assertThrows(
                ItemNotAvailableForBookingException.class,
                () -> bookingService.addBooking(bookerId, bookingInputDto)
        );

        assertEquals("Вещь с id " + itemId + " не доступна для бронирования.", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowAccessExceptionWhenUserIsOwner() {
        item.setUserId(bookerId);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.convertFromDto(any(BookingInputDto.class))).thenReturn(booking);

        AccessException exception = assertThrows(
                AccessException.class,
                () -> bookingService.addBooking(bookerId, bookingInputDto)
        );

        assertEquals("Владелец вещи не может бронировать свои вещи.", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenStartDateInPast() {
        booking.setStart(LocalDateTime.now().minusDays(1));

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.convertFromDto(any(BookingInputDto.class))).thenReturn(booking);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(bookerId, bookingInputDto)
        );

        assertEquals("Даты бронирования выбраны некорректно.", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenEndDateBeforeStartDate() {
        booking.setStart(LocalDateTime.now().plusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.convertFromDto(any(BookingInputDto.class))).thenReturn(booking);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(bookerId, bookingInputDto)
        );

        assertEquals("Даты бронирования выбраны некорректно.", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenEndDateEqualsStartDate() {
        LocalDateTime now = LocalDateTime.now().plusDays(1);
        booking.setStart(now);
        booking.setEnd(now);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.convertFromDto(any(BookingInputDto.class))).thenReturn(booking);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(bookerId, bookingInputDto)
        );

        assertEquals("Даты бронирования выбраны некорректно.", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenStartDateInPastAndEndDateValid() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.convertFromDto(any(BookingInputDto.class))).thenReturn(booking);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(bookerId, bookingInputDto)
        );

        assertEquals("Даты бронирования выбраны некорректно.", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }
}