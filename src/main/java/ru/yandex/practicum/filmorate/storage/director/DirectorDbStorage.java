package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.director.mapper.DirectorRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM director";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM director WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO director (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE director SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM director WHERE id = ?";
    private static final String DELETE_LINK_QUERY = "DELETE FROM film_directors WHERE director_id = ?";


    public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc, mapper, Director.class);
    }

    public List<Director> getDirectors() {
        return findMany(FIND_ALL_QUERY, new DirectorRowMapper());
    }

    public Director getDirector(Long id) {
        Optional<Director> director = findOne(FIND_BY_ID_QUERY, new DirectorRowMapper(), id);
        if (director.isPresent()) {
            return director.get();
        } else {
            throw new NotFoundException("Режиссер с id " + id + " не найден");
        }
    }

    public Director addDirector(Director director) {
        if (director == null) {
            throw new ValidationException("Не передано тело запроса");
        }
        long id = insert(
                INSERT_QUERY,
                director.getName()
        )[0];
        director.setId(id);
        return director;
    }

    public Director updateDirector(Director director) {
        getDirector(director.getId());
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

    public void deleteDirector(Long id) {
        getDirector(id);
        delete(DELETE_LINK_QUERY, id);
        delete(DELETE_QUERY, id);
    }
}
