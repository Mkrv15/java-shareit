package ru.practicum.shareit.booking.strategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.strategy.impl.booking.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BookingStrategyFactory {
    private final Map<State, BookerBookingFetchStrategy> strategies;

    public BookingStrategyFactory(List<BookerBookingFetchStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        BookingFetchStrategy::getState,
                        Function.identity()
                ));
    }

    public BookingFetchStrategy getStrategy(State state) {
        BookingFetchStrategy strategy = strategies.get(state);
        if (strategy == null) {
            throw new IllegalArgumentException("Booking strategy for state " + state + " is not registered");
        }
        return strategy;
    }
}

