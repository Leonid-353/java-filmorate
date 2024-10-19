package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
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

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
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
                return oldFilm;
            } else {
                log.info("Данные о фильме обновлять не требуется");
                return oldFilm;
            }
        }
        log.warn("Фильм не найден в хранилище");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
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
