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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceAddStatusBookingTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Long bookingId;
    private Long itemId;
    private Long ownerId;
    private Long bookerId;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDtoApproved;
    private BookingDto bookingDtoRejected;
    private BookingDto bookingDtoCanceled;
    private BookingDto bookingDtoWaiting;

    @BeforeEach
    void setUp() {
        bookingId = 1L;
        itemId = 2L;
        ownerId = 3L;
        bookerId = 4L;

        owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        booker = new User();
        booker.setId(bookerId);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

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

        bookingDtoApproved = new BookingDto();
        bookingDtoApproved.setId(bookingId);
        bookingDtoApproved.setStatus(Status.APPROVED);

        bookingDtoRejected = new BookingDto();
        bookingDtoRejected.setId(bookingId);
        bookingDtoRejected.setStatus(Status.REJECTED);

        bookingDtoCanceled = new BookingDto();
        bookingDtoCanceled.setId(bookingId);
        bookingDtoCanceled.setStatus(Status.CANCELED);

        bookingDtoWaiting = new BookingDto();
        bookingDtoWaiting.setId(bookingId);
        bookingDtoWaiting.setStatus(Status.WAITING);
    }

    @Test
    void shouldApproveBookingWhenOwnerApproves() {
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setStatus(Status.APPROVED);
            return savedBooking;
        });
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDtoApproved);

        BookingDto result = bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER);

        assertEquals(Status.APPROVED, result.getStatus());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
    }

    @Test
    void shouldRejectBookingWhenOwnerRejects() {
        boolean approved = false;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setStatus(Status.REJECTED);
            return savedBooking;
        });
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDtoRejected);

        BookingDto result = bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER);

        assertEquals(Status.REJECTED, result.getStatus());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenBookingNotFound() {
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER)
        );

        assertEquals("Бронирование с id " + bookingId + " не найдено", exception.getMessage());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowAccessExceptionWhenUserIsNotOwner() {
        Long notOwnerId = 5L;
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        AccessException exception = assertThrows(
                AccessException.class,
                () -> bookingService.approveOrRejectBooking(notOwnerId, bookingId, approved, AccessLevel.OWNER)
        );

        assertEquals("У пользователя с id " + notOwnerId + " нет прав на просмотр бронирования с id " + bookingId + ",",
                exception.getMessage());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenBookingStatusIsApproved() {
        booking.setStatus(Status.APPROVED);
        boolean approved = false;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER)
        );

        assertEquals("У бронирования с id " + bookingId + " уже стоит статус " + Status.APPROVED.name(),
                exception.getMessage());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenBookingAlreadyApproved() {
        booking.setStatus(Status.APPROVED);
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER)
        );

        assertEquals("У бронирования с id " + bookingId + " уже стоит статус " + Status.APPROVED.name(),
                exception.getMessage());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldNotThrowExceptionWhenBookingAlreadyRejected() {
        booking.setStatus(Status.REJECTED);
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setStatus(Status.REJECTED);
            return savedBooking;
        });
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDtoRejected);

        BookingDto result = bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER);

        assertEquals(Status.REJECTED, result.getStatus());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
    }

    @Test
    void shouldNotThrowExceptionWhenBookingAlreadyCancelled() {
        booking.setStatus(Status.CANCELED);
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setStatus(Status.CANCELED);
            return savedBooking;
        });
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDtoCanceled);

        BookingDto result = bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER);

        assertEquals(Status.CANCELED, result.getStatus());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).convertToDto(any(Booking.class));
    }

    @Test
    void shouldReturnBookingDtoWithUpdatedStatusAfterApproval() {
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setStatus(Status.APPROVED);
            return savedBooking;
        });
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDtoApproved);

        BookingDto result = bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER);

        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void shouldReturnBookingDtoWithUpdatedStatusAfterRejection() {
        boolean approved = false;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setStatus(Status.REJECTED);
            return savedBooking;
        });
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDtoRejected);

        BookingDto result = bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER);

        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void shouldNotThrowExceptionWhenBookingStatusIsWaiting() {
        booking.setStatus(Status.WAITING);
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setStatus(Status.APPROVED);
            return savedBooking;
        });
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDtoApproved);

        BookingDto result = bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER);

        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void shouldReturnWaitingStatusWhenBookingIsWaiting() {
        booking.setStatus(Status.WAITING);
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setStatus(Status.APPROVED);
            return savedBooking;
        });
        when(bookingMapper.convertToDto(any(Booking.class))).thenReturn(bookingDtoApproved);

        BookingDto result = bookingService.approveOrRejectBooking(ownerId, bookingId, approved, AccessLevel.OWNER);

        assertEquals(Status.APPROVED, result.getStatus());
    }
}