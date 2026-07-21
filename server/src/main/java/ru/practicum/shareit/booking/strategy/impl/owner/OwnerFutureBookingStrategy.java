package ru.practicum.shareit.booking.strategy.impl.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OwnerFutureBookingStrategy implements OwnerBookingFetchStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public State getState() {
        return State.FUTURE;
    }

    @Override
    public List<Booking> findBookings(Long ownerId, Pageable pageable) {
        return bookingRepository.findAllByOwnerId(ownerId, pageable);
    }
}
