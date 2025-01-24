package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> findAllFilms() {
        return filmService.findAllFilmsDto();
    }

    @GetMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public FilmDto findFilm(@PathVariable("filmId") Long filmId) {
        return filmService.findFilmDto(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto createFilm(@Valid @RequestBody NewFilmRequest newFilmRequest) {
        return filmService.createFilm(newFilmRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest newFilmRequest) {
        return filmService.updateFilm(newFilmRequest);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFilm(@PathVariable("filmId") Long filmId) {
        filmService.removeFilm(filmId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> findPopularFilms(@RequestParam(defaultValue = "10") Long count) {
        return filmService.findPopularFilms(count);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void likeIt(@PathVariable("filmId") Long filmId,
                       @PathVariable("userId") Long userId) {
        filmService.likeIt(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikes(@PathVariable("filmId") Long filmId,
                            @PathVariable("userId") Long userId) {
        filmService.removeLikes(filmId, userId);
    }

}
