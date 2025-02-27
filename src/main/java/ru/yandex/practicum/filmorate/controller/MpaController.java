package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RequestMapping("/mpa")
@RestController
public class MpaController {
    private final FilmService filmService;

    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Mpa> findAllMpa() {
        return filmService.findAllMpa();
    }

    @GetMapping("/{mpaId}")
    public Mpa findMpaById(@PathVariable @Positive Long mpaId) {
        return filmService.findMpaById(mpaId);
    }
}
