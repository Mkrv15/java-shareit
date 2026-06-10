package ru.practicum.shareit.booking.strategy;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingFetchStrategy {
    List<Booking> execute(Long bookerId, Sort sort);
}

