package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.validation.FilmMinimumReleaseDate;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotNull
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    @NotNull
    private String description;
    @FilmMinimumReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
    private Set<Genre> genres;
    private Set<Director> directors;
    private Mpa mpa;

    public boolean hasId() {
        return id != null;
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasGenres() {
        return CollectionUtils.isNotEmpty(genres);
    }

    public boolean hasMpa() {
        return mpa != null;
    }
}
