package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> getDirectors();

    Director getDirector(Long id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long id);
}
