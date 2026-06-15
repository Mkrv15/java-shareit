package ru.practicum.shareit.booking.strategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.strategy.impl.booking.*;

@Component
public class BookingStrategyFactory {
    private final BookingRepository bookingRepository;

    public BookingStrategyFactory(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public BookingFetchStrategy getStrategy(State state) {
        switch (state) {
            case WAITING:
                return new WaitingBookingStrategy(bookingRepository);
            case REJECTED:
                return new RejectedBookingStrategy(bookingRepository);
            case PAST:
                return new PastBookingStrategy(bookingRepository);
            case FUTURE:
                return new FutureBookingStrategy(bookingRepository);
            case CURRENT:
                return new CurrentBookingStrategy(bookingRepository);
            default:
                return new AllBookingStrategy(bookingRepository);
        }
    }
}
