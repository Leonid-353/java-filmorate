package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        film.setDirectors(request.getDirectors());
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
        dto.setGenres(film.getGenres() == null ? new HashSet<>() : film.getGenres());
        dto.setMpa(film.getMpa());
        dto.setDirectors(film.getDirectors() == null ? new HashSet<>() : film.getDirectors());
        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.getId() != null) {
            film.setName(request.getName());
            film.setDescription(request.getDescription());
            film.setReleaseDate(request.getReleaseDate());
            film.setDuration(request.getDuration());
            film.setGenres(request.getGenres());
            film.setMpa(request.getMpa());
            film.setDirectors(request.getDirectors());
        } else {
            throw new ValidationException("не задан id фильма, который требуется обновить");
        }
        return film;
    }
}
