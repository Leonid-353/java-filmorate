package ru.yandex.practicum.filmorate.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Not found {}", e.getMessage(), e);
        return new ErrorResponse("Не найдено", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedDataException(final DuplicatedDataException e) {
        log.error("Duplicated data {}", e.getMessage(), e);
        return new ErrorResponse("Дублированные данные", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleConditionsNotMetException(final ConditionsNotMetException e) {
        log.error("Conditions not met {}", e.getMessage(), e);
        return new ErrorResponse("Условия не выполнены", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(final NoSuchElementException e) {
        log.error("No such element {}", e.getMessage(), e);
        return new ErrorResponse("Такого элемента нет", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        log.error("Bad Request {}", e.getMessage(), e);
        return new ErrorResponse("Неверный запрос пользователя", e.getMessage());
    }
}
