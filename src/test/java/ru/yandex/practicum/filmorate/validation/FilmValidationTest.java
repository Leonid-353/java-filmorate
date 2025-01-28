package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.time.LocalDate;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
class FilmValidationTest {
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();
    Film film;

    @BeforeEach
    void beforeEach() {
        film = new Film();
        film.setId(1L);
        film.setName("Film test");
        film.setDescription("Description test");
        film.setReleaseDate(LocalDate.of(1997, 12, 31));
        film.setDuration(100);
    }

    @DisplayName("Валидация поля name")
    @ParameterizedTest
    @CsvSource({
            "'', Название не может быть пустым",
            "null, Название не может быть пустым",
            "Форрест Гамп, OK"
    })
    void nameValidation(String testName, String expectedErrorMessage) {
        if ("null".equals(testName)) {
            testName = null;
        }
        film.setName(testName);
        List<String> violations = validator.validate(film).stream()
                .map(ConstraintViolation::getMessage)
                .filter(expectedErrorMessage::equals)
                .toList();

        if (!violations.isEmpty()) {
            Assertions.assertEquals(expectedErrorMessage, violations.getFirst());
        }
    }

    @DisplayName("Валидация поля description")
    @ParameterizedTest
    @CsvSource({
            "0, OK",
            "1, OK",
            "199, OK",
            "200, OK",
            "201, Максимальная длина описания — 200 символов"
    })
    void descriptionValidation(int lengthDescription, String expectedErrorMessage) {
        String testDescription = "d".repeat(lengthDescription);
        film.setDescription(testDescription);
        List<String> violations = validator.validate(film).stream()
                .map(ConstraintViolation::getMessage)
                .filter(expectedErrorMessage::equals)
                .toList();

        if (!violations.isEmpty()) {
            Assertions.assertEquals(expectedErrorMessage, violations.getFirst());
        }
    }

    @DisplayName("Валидация поля releaseDate")
    @ParameterizedTest
    @CsvSource({
            "1800-01-01, Дата релиза — не раньше 28 декабря 1895 года",
            "1895-12-27, Дата релиза — не раньше 28 декабря 1895 года",
            "1895-12-28, Дата релиза — не раньше 28 декабря 1895 года",
            "1895-12-29, OK",
            "2024-10-14, OK",
    })
    void releaseDateValidation(LocalDate releaseDate, String expectedErrorMessage) {
        film.setReleaseDate(releaseDate);
        List<String> violations = validator.validate(film).stream()
                .map(ConstraintViolation::getMessage)
                .filter(expectedErrorMessage::equals)
                .toList();

        if (!violations.isEmpty()) {
            Assertions.assertEquals(expectedErrorMessage, violations.getFirst());
        }
    }

    @DisplayName("Валидация поля duration")
    @ParameterizedTest
    @CsvSource({
            "-1, Продолжительность фильма должна быть положительным числом",
            "0, Продолжительность фильма должна быть положительным числом",
            "1, OK",
            "100, OK"
    })
    void durationValidation(Integer duration, String expectedErrorMessage) {
        film.setDuration(duration);
        List<String> violations = validator.validate(film).stream()
                .map(ConstraintViolation::getMessage)
                .filter(expectedErrorMessage::equals)
                .toList();

        if (!violations.isEmpty()) {
            Assertions.assertEquals(expectedErrorMessage, violations.getFirst());
        }
    }
}
