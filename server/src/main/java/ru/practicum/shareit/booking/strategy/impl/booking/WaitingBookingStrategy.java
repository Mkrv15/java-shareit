package ru.practicum.shareit.booking.strategy.impl.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WaitingBookingStrategy implements BookerBookingFetchStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public State getState() {
        return State.WAITING;
    }

    @Override
    public List<Booking> findBookings(Long bookerId, Pageable pageable) {
        return bookingRepository.findAllByBookerIdAndStatus(
                bookerId, Status.WAITING, pageable);
    }
}
