package ru.practicum.shareit.booking.strategy.impl;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.strategy.BookingFetchStrategy;

import java.util.List;

public class WaitingBookingStrategy implements BookingFetchStrategy {
    private final BookingRepository bookingRepository;

    public WaitingBookingStrategy(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> execute(Long bookerId, Sort sort) {
        return bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING, sort);
    }
}
