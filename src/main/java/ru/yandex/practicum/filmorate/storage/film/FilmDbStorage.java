package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmRowMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_ALL_LIKES = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String FIND_ALL_GENRE = "SELECT * FROM genre WHERE id IN (SELECT genre_id " +
            "FROM film_genre " +
            "WHERE film_id = ?)";
    private static final String FIND_MPA = "SELECT * FROM mpa WHERE id IN (SELECT mpa_id " +
            "FROM films " +
            "WHERE id = ?)";
    private static final String FIND_BY_ID_GENRE = "SELECT name FROM genre WHERE id = ?";
    private static final String FIND_BY_ID_MPA = "SELECT name FROM mpa WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre (film_Id, genre_Id)" +
            "VALUES (?, ?)";
    private static final String INSERT_LIKE_IT_QUERY = "INSERT INTO likes (film_id, user_id)" +
            "VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
            "WHERE id = ?";
    private static final String UPDATE_GENRE_QUERY = "UPDATE film_genre " +
            "SET film_id = ?, genre_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String DELETE_LIKES_BY_FILM_ID_QUERY = "DELETE FROM likes WHERE film_id = ?";
    private static final String DELETE_LIKES_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper, Film.class);
    }

    @Override
    public Collection<Film> findAllFilms() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(film -> film.setLikes(new HashSet<>(findManyId(FIND_ALL_LIKES, film.getId()))));
        films.forEach(film -> film.setGenres(new HashSet<>(findManyGenre(FIND_ALL_GENRE, film.getId()))));
        return films;
    }

    public Optional<String> findMpaName(Long mpaId) {
        if (mpaId < 1 || mpaId > 5) {
            throw new NotFoundException("Рейтинг MPA c id = " + mpaId + " не существует.");
        } else {
            return findOneString(FIND_BY_ID_MPA, mpaId);
        }
    }

    public Optional<String> findGenreName(Long genreId) {
        if (genreId < 1 || genreId > 6) {
            throw new NotFoundException("Жанр с id = " + genreId + " не существует.");
        } else {
            return findOneString(FIND_BY_ID_GENRE, genreId);
        }
    }

    @Override
    public Optional<Film> findFilm(Long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId)
                .map(film -> {
                    film.setLikes(new HashSet<>(findManyId(FIND_ALL_LIKES, film.getId())));
                    return film;
                })
                .map(film -> {
                    film.setGenres(new HashSet<>(findManyGenre(FIND_ALL_GENRE, film.getId())));
                    return film;
                })
                .map(film -> {
                    film.setMpa(findOneMpa(FIND_MPA, film.getId()).orElseThrow());
                    return film;
                });
    }

    @Override
    public Film createFilm(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                insert(
                        INSERT_FILM_GENRE_QUERY,
                        film.getId(),
                        genre.getId()
                );
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(
                UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        for (Genre genre : newFilm.getGenres()) {
            update(
                    UPDATE_GENRE_QUERY,
                    newFilm.getId(),
                    genre.getId()
            );
        }

        return newFilm;
    }

    @Override
    public void removeFilm(Long filmId) {
        delete(DELETE_QUERY, filmId);
        delete(DELETE_FILM_GENRE_QUERY, filmId);
        delete(DELETE_LIKES_BY_FILM_ID_QUERY, filmId);
    }

    public void likeIt(Long filmId, Long userId) {
        insert(
                INSERT_LIKE_IT_QUERY,
                filmId,
                userId
        );
    }

    public void removeLikes(Long filmId, Long userId) {
        delete(DELETE_LIKES_QUERY, filmId, userId);
    }
}
