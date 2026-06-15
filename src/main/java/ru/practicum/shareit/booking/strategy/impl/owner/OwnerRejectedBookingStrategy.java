package ru.practicum.shareit.booking.strategy.impl.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.strategy.BookingFetchStrategy;

import java.util.List;

@RequiredArgsConstructor
public class OwnerRejectedBookingStrategy implements BookingFetchStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> execute(Long ownerId, Sort sort) {
        return bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.REJECTED, sort);
    }
}
