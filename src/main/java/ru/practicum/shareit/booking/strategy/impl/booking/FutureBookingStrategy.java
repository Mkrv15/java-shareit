package ru.practicum.shareit.booking.strategy.impl.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FutureBookingStrategy implements BookerBookingFetchStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public State getState() {
        return State.FUTURE;
    }

    @Override
    public List<Booking> findBookings(Long bookerId, Sort sort) {
        return bookingRepository.findAllByBookerIdAndStartAfter(
                bookerId, LocalDateTime.now(), sort);
    }
}
