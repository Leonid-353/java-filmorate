package ru.yandex.practicum.filmorate.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(final String message) {
        super(message);
    }

    public BadRequestException(final String message, Throwable e) {
        super(message, e);
    }
}
