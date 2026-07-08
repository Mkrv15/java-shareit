package ru.practicum.shareit.booking.strategy;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingFetchStrategy {
    State getState();

    List<Booking> findBookings(Long bookerId, Pageable pageable);
}

