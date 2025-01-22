package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAllFilms();

    Optional<Film> findFilm(Long filmId);

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    void removeFilm(Long filmId);
}
