package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Headers;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(Headers.USER_ID_HEADER) long userId,
                                 @Valid @RequestBody BookingInputDto bookingInputDto) {
        return bookingService.addBooking(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                             @RequestHeader(Headers.USER_ID_HEADER) long userId) {
        return bookingService.approveOrRejectBooking(userId, bookingId, approved, AccessLevel.OWNER);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId, @RequestHeader(Headers.USER_ID_HEADER) long userId) {
        return bookingService.getBooking(bookingId, userId, AccessLevel.OWNER_AND_BOOKER);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfCurrentUser(@RequestParam(defaultValue = "ALL") String state,
                                                     @RequestHeader(Headers.USER_ID_HEADER) long userId) {
        return bookingService.getBookingsOfCurrentUser(State.convert(state), userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(Headers.USER_ID_HEADER) long userId) {
        return bookingService.getBookingsOfOwner(State.convert(state), userId);
    }
}
