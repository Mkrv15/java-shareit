package ru.practicum.shareit.booking.strategy.impl.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OwnerRejectedBookingStrategy implements OwnerBookingFetchStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public State getState() {
        return State.REJECTED;
    }

    @Override
    public List<Booking> findBookings(Long ownerId, Sort sort) {
        return bookingRepository.findAllByOwnerId(ownerId, sort);
    }
}
