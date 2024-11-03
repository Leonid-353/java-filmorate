package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film findFilm(Long filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Запрос на получение. Фильм с id: {} не найден в хранилище", filmId);
            throw new NotFoundException(String.format("Фильм с id: %d не найден в хранилище", filmId));
        }
        return films.get(filmId);
    }

    @Override
    public Film createFilm(Film film) {
        for (Map.Entry<Long, Film> entry : films.entrySet()) {
            log.debug("Рассматривается фильм: {}", entry.getValue().getName());
            if (entry.getValue().getName().equals(film.getName())) {
                log.warn("Попытка добавления уже существующего фильма");
                throw new DuplicatedDataException("Фильм с таким названием уже существует");
            }
        }
        film.setId(getNextId());
        log.debug("Для фильма установлен id: {}", film.getId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Id фильма не указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            log.trace("Фильм найден в хранилище");
            Film oldFilm = films.get(newFilm.getId());
            if (!newFilm.equals(oldFilm)) {
                log.trace("Обновление данных о фильме");
                oldFilm.setName(newFilm.getName());
                oldFilm.setDescription(newFilm.getDescription());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                oldFilm.setDuration(newFilm.getDuration());
                log.info("Данные о фильме успешно обновлены");
            } else {
                log.info("Данные о фильме обновлять не требуется");
            }
            return oldFilm;
        }
        log.warn("Фильм не найден в хранилище");
        throw new NotFoundException(String.format("Фильм с id: %d не найден в хранилище", newFilm.getId()));
    }

    @Override
    public void removeFilm(Long filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Запрос на удаление. Фильм с id: {} не найден в хранилище", filmId);
            throw new NotFoundException(String.format("Фильм с id: %d не найден в хранилище", filmId));
        }
        films.remove(filmId);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
