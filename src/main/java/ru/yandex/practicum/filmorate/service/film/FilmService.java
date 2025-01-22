package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class FilmService {
    final FilmDbStorage filmDbStorage;
    final UserDbStorage userDbStorage;
    final GenreDbStorage genreDbStorage;
    final MpaDbStorage mpaDbStorage;

    public FilmService(/*@Qualifier("filmDbStorage") */FilmDbStorage filmDbStorage,
            /*@Qualifier("userDbStorage") */UserDbStorage userDbStorage,
                                                       GenreDbStorage genreDbStorage,
                                                       MpaDbStorage mpaDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    // Получение всех фильмов
    public Collection<FilmDto> findAllFilmsDto() {
        return filmDbStorage.findAllFilms().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    // Получение фильма по id
    public FilmDto findFilmDto(Long filmId) {
        return filmDbStorage.findFilm(filmId)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow();
    }

    // Создание нового фильма
    public FilmDto createFilm(NewFilmRequest newFilmRequest) {
        Film film = FilmMapper.mapToFilm(newFilmRequest);
        Mpa filmMpa = film.getMpa();
        filmMpa.setId(filmDbStorage.findMpaId(newFilmRequest.getMpa()).orElseThrow());
        filmMpa.setName(newFilmRequest.getMpa());
        film.setMpa(filmMpa);
        filmDbStorage.createFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    // Изменение существующего фильма
    public FilmDto updateFilm(UpdateFilmRequest updateFilmRequest) {
        Film updateFilm = filmDbStorage.findFilm(updateFilmRequest.getId())
                .map(film -> FilmMapper.updateFilmFields(film, updateFilmRequest))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        Mpa filmMpa = updateFilm.getMpa();
        filmMpa.setId(filmDbStorage.findMpaId(updateFilmRequest.getMpa()).orElseThrow());
        filmMpa.setName(updateFilmRequest.getMpa());
        updateFilm.setMpa(filmMpa);
        updateFilm = filmDbStorage.updateFilm(updateFilm);
        return FilmMapper.mapToFilmDto(updateFilm);
    }

    // Удаление фильма по id
    public void removeFilm(Long filmId) {
        filmDbStorage.removeFilm(filmId);
    }

    // Получение популярных фильмов
    public Collection<FilmDto> findPopularFilms(Long count) {
        return filmDbStorage.findAllFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesSize).reversed())
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    // Поставить лайк
    public void likeIt(Long filmId, Long userId) {
        Film film = filmDbStorage.findFilm(filmId).orElseThrow();
        User user = userDbStorage.findUser(userId).orElseThrow();
        if (film.addUserIdInLikes(user.getId())) {
            filmDbStorage.likeIt(filmId, userId);
        }
    }

    // Удалить лайк
    public void removeLikes(Long filmId, Long userId) {
        Film film = filmDbStorage.findFilm(filmId).orElseThrow();
        User user = userDbStorage.findUser(userId).orElseThrow();
        if (film.removeUserIdInLikes(user.getId())) {
            filmDbStorage.removeLikes(filmId, userId);
        }
    }

    // Получить список всех жанров
    public Collection<Genre> findAllGenres() {
        return genreDbStorage.findAllGenres().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    // Получить жанр по id
    public Genre findGenreById(Long genreId) {
        return genreDbStorage.findGenreById(genreId).orElseThrow();
    }

    // Получить список всех возрастных рейтингов mpa
    public Collection<Mpa> findAllMpa() {
        return mpaDbStorage.findAllMpa().stream()
                .sorted(Comparator.comparing(Mpa::getId))
                .collect(Collectors.toList());
    }

    // Получить возрастной рейтинг mpa по id
    public Mpa findMpaById(Long mpaId) {
        return mpaDbStorage.findMpaById(mpaId).orElseThrow();
    }
}
