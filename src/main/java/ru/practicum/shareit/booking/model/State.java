package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static State convert(String source) {
        try {
            return State.valueOf(source);
        } catch (Exception e) {
            String message = String.format("Unknown state: %S", source);
            throw new ValidationException(message);
        }
    }
}
