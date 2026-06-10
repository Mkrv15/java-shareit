package ru.practicum.shareit.booking.strategy;

import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Component
public class BookingStrategyContext {

    @Setter
    private BookingFetchStrategy strategy;

    public List<Booking> executeStrategy(Long bookerId, Sort sort) {
        return strategy.execute(bookerId, sort);
    }
}
