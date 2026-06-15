package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.strategy.impl.owner.*;

@Component
@RequiredArgsConstructor
public class OwnerBookingStrategyFactory {

    private final BookingRepository bookingRepository;

    public BookingFetchStrategy getStrategy(State state) {
        switch (state) {
            case WAITING:
                return new OwnerWaitingBookingStrategy(bookingRepository);
            case REJECTED:
                return new OwnerRejectedBookingStrategy(bookingRepository);
            case PAST:
                return new OwnerPastBookingStrategy(bookingRepository);
            case FUTURE:
                return new OwnerFutureBookingStrategy(bookingRepository);
            case CURRENT:
                return new OwnerCurrentBookingStrategy(bookingRepository);
            default:
                return new OwnerAllBookingStrategy(bookingRepository);
        }
    }
}
