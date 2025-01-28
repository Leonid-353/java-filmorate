package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.HashSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setLikes(new HashSet<>());
        film.setGenres(request.getGenres());
        film.setMpa(request.getMpa());

        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setLikes(film.getLikesSize());
        dto.setGenres(film.getGenres());
        dto.setMpa(film.getMpa());

        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasId()) {
            if (request.hasName()) {
                film.setName(request.getName());
            }
            if (request.hasDescription()) {
                film.setDescription(request.getDescription());
            }
            if (request.hasReleaseDate()) {
                film.setReleaseDate(request.getReleaseDate());
            }
            if (request.hasDuration()) {
                film.setDuration(request.getDuration());
            }
            if (request.hasGenres()) {
                film.setGenres(request.getGenres());
            }
            if (request.hasMpa()) {
                film.setMpa(request.getMpa());
            }
        }

        return film;
    }
}
