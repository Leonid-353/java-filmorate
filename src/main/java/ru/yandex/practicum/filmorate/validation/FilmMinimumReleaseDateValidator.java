package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class FilmMinimumReleaseDateValidator implements ConstraintValidator<FilmMinimumReleaseDate, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(FilmMinimumReleaseDate constraintAnnotation) {
        minDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.isAfter(minDate);
    }
}
