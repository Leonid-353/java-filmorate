package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.director.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.mapper.GenreRowMapper;

import java.util.*;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.*, m.id  as mpa_id, m.name as mpa_name  FROM films f " +
            "LEFT JOIN mpa m on m.id = f.mpa_id ";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.id  as mpa_id, m.name as mpa_name  FROM films f " +
            "LEFT JOIN mpa m on m.id = f.mpa_id " +
            " WHERE f.id = ?";
    private static final String FIND_ALL_LIKES = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String FIND_ALL_GENRE = "SELECT * FROM genre WHERE id IN (SELECT genre_id " +
            "FROM film_genre " +
            "WHERE film_id = ?)";
    private static final String FIND_ALL_DIRECTORS = "SELECT d.* FROM director as d " +
            "left join film_directors as fd on fd.director_id = d.id " +
            "where fd.film_id = ?";
    private static final String FIND_ALL_FILMS_BY_DIRECTOR_ORDER_BY_LIKES = "SELECT f.*, m.id  as mpa_id, m.name as mpa_name FROM films as f " +
            "JOIN film_directors as fd on fd.film_id = f.id " +
            "left join PUBLIC.LIKES L on f.ID = L.FILM_ID " +
            "LEFT JOIN mpa m on m.id = f.mpa_id " +
            "where fd.director_id = ? " +
            "GROUP BY f.ID " +
            "ORDER BY COUNT(l.FILM_ID) DESC";
    private static final String FIND_ALL_FILMS_BY_DIRECTOR_ORDER_BY_RELEASE = "SELECT f.*,  m.name as mpa_name FROM films as f " +
            "JOIN film_directors as fd on fd.film_id = f.id " +
            "LEFT JOIN mpa m on m.id = f.mpa_id " +
            "where fd.director_id = ? " +
            "ORDER BY f.RELEASE_DATE";
    private static final String FIND_BY_ID_GENRE = "SELECT name FROM genre WHERE id = ?";
    private static final String FIND_BY_ID_MPA = "SELECT name FROM mpa WHERE id = ?";
    private static final String FIND_FILM_ID_IN_FILM_GENRE = "SELECT film_id FROM film_genre WHERE film_id = ?";
    private static final String FIND_FILM_ID_IN_FILM_DIRECTOR = "SELECT film_id FROM film_directors WHERE film_id = ?";
    private static final String FIND_FILM_ID_IN_LIKES = "SELECT film_id FROM likes WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String SEARCH_FILMS_BY_TITLE_OR_DIRECTOR_NAME = "SELECT f.*,  m.name as mpa_name FROM films f " +
            "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
            "LEFT JOIN director d ON fd.director_id = d.id " +
            "LEFT JOIN mpa m on m.id = f.mpa_id " +
            "LEFT JOIN likes l ON l.film_id = f.id " +
            "WHERE %s " +
            "GROUP BY f.ID " +
            "ORDER BY COUNT(l.FILM_ID) DESC";
    private static final String SEARCH_FILMS_BY_GENRE_YEAR = "SELECT * FROM films AS f " +
            "LEFT JOIN likes AS l ON l.film_id = f.id " +
            "LEFT JOIN film_genre AS fg ON fg.film_id = f.id " +
            "WHERE fg.genre_id = ? and (f.release_date BETWEEN '?-01-01' AND '?-12-31')";
    private static final String SEARCH_FILMS_BY_YEAR = "SELECT * FROM films AS f " +
            "LEFT JOIN likes AS l ON l.film_id = f.id " +
            "LEFT JOIN film_genre AS fg ON fg.film_id = f.id " +
            "WHERE f.release_date BETWEEN '?-01-01' AND '?-12-31'";
    private static final String SEARCH_FILMS_BY_GENRE = "SELECT f.* FROM films AS f " +
            "LEFT JOIN likes AS l ON l.film_id = f.id " +
            "LEFT JOIN film_genre AS fg ON fg.film_id = f.id " +
            "WHERE fg.genre_id = ?";
    private static final String SEARCH_PARAM_DIRECTOR_NAME = " D.NAME like ?";
    private static final String SEARCH_PARAM_FILM_NAME = " F.NAME like ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre (film_Id, genre_Id)" +
            "VALUES (?, ?)";
    private static final String INSERT_FILM_DIRECTOR_QUERY = "INSERT INTO film_directors (film_Id, director_Id)" +
            "VALUES (?, ?)";
    private static final String INSERT_LIKE_IT_QUERY = "INSERT INTO likes (film_id, user_id)" +
            "VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
            "WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String DELETE_FILM_DIRECTOR_QUERY = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String DELETE_LIKES_BY_FILM_ID_QUERY = "DELETE FROM likes WHERE film_id = ?";
    private static final String DELETE_LIKES_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_LIKES_FILMS_FOR_USER = "SELECT f.*, m.name as mpa_name FROM films f " +
            "JOIN likes l ON f.id = l.film_id " +
            "LEFT JOIN mpa m on m.id = f.mpa_id " +
            "WHERE l.user_id = ?";
    private static final String FIND_COMMON_FILMS = "SELECT f.*, m.name AS mpa_name " +
            "FROM FILMS f " +
            "LEFT JOIN mpa m ON M.ID = F.MPA_ID " +
            "WHERE f.ID IN (" +
            "SELECT FILM_ID FROM likes " +
            "WHERE USER_ID = ? OR USER_ID = ? " +
            "GROUP BY FILM_ID " +
            "HAVING count(FILM_ID)>=2)";
    private static final String ORDER_BY_LIKES = "likes";
    private static final String ORDER_BY_RELEASE = "year";
    private static final String TITLE = "title";
    private static final String DIRECTOR = "director";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper, Film.class);
    }

    @Override
    public Collection<Film> findAllFilms() {
        List<Film> films = findMany(FIND_ALL_QUERY, new FilmRowMapper());
        return initializeDataFromLinkedTables(films);
    }

    public Collection<Film> findFilmsByDirectorId(Long directorId, String orderType) {
        List<Film> films;
        if (orderType.equals(ORDER_BY_RELEASE)) {
            films = findMany(FIND_ALL_FILMS_BY_DIRECTOR_ORDER_BY_RELEASE, new FilmRowMapper(), directorId);
        } else if (orderType.equals(ORDER_BY_LIKES)) {
            films = findMany(FIND_ALL_FILMS_BY_DIRECTOR_ORDER_BY_LIKES, new FilmRowMapper(), directorId);
        } else {
            throw new IllegalArgumentException("Invalid order type: " + orderType);
        }
        return initializeDataFromLinkedTables(films);
    }

    public Collection<Film> findFilmsByTitleOrDirectorName(String query, String searchParam) {
        String[] searchParams = searchParam.split(",");
        String preparedSearchValue = "%" + query + "%";
        Object[] searchValues = new String[searchParams.length];
        Arrays.fill(searchValues, preparedSearchValue);
        if (searchParams.length == 0 || Arrays.stream(searchParams)
                .anyMatch(it -> !it.equals(TITLE) && !it.equals(DIRECTOR))) {
            throw new BadRequestException("Invalid search parameter: " + searchParam);
        } else {
            searchParam = String.join(" OR ", searchParams)
                    .replace(TITLE, SEARCH_PARAM_FILM_NAME)
                    .replace(DIRECTOR, SEARCH_PARAM_DIRECTOR_NAME);
        }
        String searchQuery = String.format(SEARCH_FILMS_BY_TITLE_OR_DIRECTOR_NAME, searchParam);
        List<Film> films;
        films = findMany(searchQuery, new FilmRowMapper(), searchValues);
        return initializeDataFromLinkedTables(films);
    }

    public Collection<Film> initializeDataFromLinkedTables(List<Film> films) {
        films.forEach(film -> film.setLikes(new HashSet<>(findManyId(FIND_ALL_LIKES, film.getId()))));
        films.forEach(film -> film.setGenres(new HashSet<>(findMany(FIND_ALL_GENRE, new GenreRowMapper(), film.getId()))));
        films.forEach(film -> film.setDirectors(new HashSet<>(findMany(FIND_ALL_DIRECTORS, new DirectorRowMapper(), film.getId()))));
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
        return findOne(FIND_BY_ID_QUERY, new FilmRowMapper(), filmId)
                .map(film -> {
                    film.setLikes(new HashSet<>(findManyId(FIND_ALL_LIKES, film.getId())));
                    return film;
                })
                .map(film -> {
                    film.setGenres(new HashSet<>(findMany(FIND_ALL_GENRE, new GenreRowMapper(), film.getId())));
                    return film;
                })
                .map(film -> {
                    film.setDirectors(new HashSet<>(findMany(FIND_ALL_DIRECTORS, new DirectorRowMapper(), film.getId())));
                    return film;
                })
                ;
    }

    @Override
    public Film createFilm(Film film) {
        long id;
        id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId()
        )[0];
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
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                insert(INSERT_FILM_DIRECTOR_QUERY,
                        film.getId(),
                        director.getId());
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
        update(DELETE_FILM_GENRE_QUERY, newFilm.getId());
        for (Genre genre : newFilm.getGenres()) {
            insert(
                    INSERT_FILM_GENRE_QUERY,
                    newFilm.getId(),
                    genre.getId()
            );
        }
        update(DELETE_FILM_DIRECTOR_QUERY, newFilm.getId());
        for (Director director : newFilm.getDirectors()) {
            insert(INSERT_FILM_DIRECTOR_QUERY,
                    newFilm.getId(),
                    director.getId());
        }
        return newFilm;
    }

    @Override
    public void removeFilm(Long filmId) {
        update(DELETE_FILM_GENRE_QUERY, filmId);
        update(DELETE_FILM_DIRECTOR_QUERY, filmId);
        update(DELETE_LIKES_BY_FILM_ID_QUERY, filmId);
        update(DELETE_QUERY, filmId);
    }

    public void likeIt(Long filmId, Long userId) {
        insert(
                INSERT_LIKE_IT_QUERY,
                filmId,
                userId
        );
    }

    public void removeLikes(Long filmId, Long userId) {
        update(DELETE_LIKES_QUERY, filmId, userId);
    }

    public Collection<Film> findFilmsLike(Long userId) {
        return findMany(FIND_LIKES_FILMS_FOR_USER, new FilmRowMapper(), userId);
    }

    public List<Film> findCommonFilms(Long userId, Long friendId) {
        return findMany(FIND_COMMON_FILMS, new FilmRowMapper(), userId, friendId);
    }

    public Collection<Film> findFilmsByGenreYear(Long genreId, Long year) {

        if (year != null && genreId != null) {
            return findMany(SEARCH_FILMS_BY_GENRE_YEAR, new FilmRowMapper(), genreId, year, year);
        } else if (genreId != null) {
            return findMany(SEARCH_FILMS_BY_GENRE, new FilmRowMapper(), genreId);
        } else if (year != null) {
            return findMany(SEARCH_FILMS_BY_YEAR, new FilmRowMapper(), year);
        } else {
            return findMany(FIND_ALL_QUERY, new FilmRowMapper());
        }


    }



}
