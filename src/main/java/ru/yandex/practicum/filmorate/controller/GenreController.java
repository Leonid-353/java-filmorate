package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RequestMapping("/genres")
@RestController
public class GenreController {
    private final FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Genre> findAllGenres() {
        return filmService.findAllGenres();
    }

    @GetMapping("/{genreId}")
    public Genre findGenreById(@PathVariable Long genreId) {
        return filmService.findGenreById(genreId);
    }
}
