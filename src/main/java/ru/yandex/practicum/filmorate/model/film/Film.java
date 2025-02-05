package ru.yandex.practicum.filmorate.model.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validation.FilmMinimumReleaseDate;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    @NotNull
    String description;
    @FilmMinimumReleaseDate
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    Integer duration;
    Set<Long> likes = new LinkedHashSet<>();
    Set<Genre> genres = new LinkedHashSet<>();
    Set<Director> directors = new LinkedHashSet<>();
    Mpa mpa;

    public boolean addUserIdInLikes(Long userId) {
        return likes.add(userId);
    }

    public boolean removeUserIdInLikes(Long userId) {
        return likes.remove(userId);
    }

    public Long getLikesSize() {
        return (long) likes.size();
    }
}
