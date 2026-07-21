package ru.practicum.shareit.exception;

public class ItemNotAvailableForBookingException extends RuntimeException {
    public ItemNotAvailableForBookingException(String message) {
        super(message);
    }
}
