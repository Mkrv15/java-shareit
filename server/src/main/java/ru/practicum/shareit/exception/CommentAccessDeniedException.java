package ru.practicum.shareit.exception;

public class CommentAccessDeniedException extends RuntimeException {
    public CommentAccessDeniedException(String message) {
        super(message);
    }
}
