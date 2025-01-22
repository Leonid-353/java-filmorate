package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Mpa;

//@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setGenres(request.getGenres());
        film.setMpa(new Mpa());

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
        dto.setGenre(film.getGenres());
        dto.setMpa(film.getMpa().getName());

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
            if (request.hasGenre()) {
                film.setGenres(request.getGenre());
            }
            if (request.hasMpa()) {
                film.setMpa(new Mpa());
            }
        }

        return film;
    }
}
