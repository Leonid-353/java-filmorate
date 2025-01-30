package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final FilmService filmService;

    public DirectorController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Director> getDirectors() {
        return filmService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Long id) {
        return filmService.getDirectorById(id);
    }

    @PostMapping
    public Director addDirector(@Validated @RequestBody Director director) {
        return filmService.addDirector(director);
    }


    @PutMapping
    public Director updateDirector(@Validated @RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        filmService.deleteDirector(id);
    }
}
