package ru.yandex.practicum.filmorate.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message) {
        super(message);
    }

    public NotFoundException(final String message, Throwable e) {
        super(message, e);
    }
}
