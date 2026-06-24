package ru.practicum.shareit.booking.strategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.strategy.impl.owner.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OwnerBookingStrategyFactory {

    private final Map<State, OwnerBookingFetchStrategy> strategies;

    public OwnerBookingStrategyFactory(List<OwnerBookingFetchStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        OwnerBookingFetchStrategy::getState,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        () -> new EnumMap<>(State.class)
                ));
    }

    public OwnerBookingFetchStrategy getStrategy(State state) {
        OwnerBookingFetchStrategy strategy = strategies.get(state);
        if (strategy == null) {
            throw new IllegalArgumentException("Owner booking strategy for state " + state + " is not registered");
        }
        return strategy;
    }
}
