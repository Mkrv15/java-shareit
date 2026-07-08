package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static State parse(String raw) {
        try {
            return valueOf(raw.toUpperCase());
        } catch (Exception e) {
            String message = String.format("Unknown state: %S", raw);
            throw new ValidationException(message);
        }
    }
}
