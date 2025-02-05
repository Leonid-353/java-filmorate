package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;
    private final Class<T> entityType;

    protected <R> Optional<R> findOne(String query, RowMapper<R> mapper, Object... params) {
        try {
            R result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected Optional<Long> findOneId(String query, Object... params) {
        try {
            Long result = jdbc.queryForObject(query, Long.class, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected Optional<String> findOneString(String query, Object... params) {
        try {
            String result = jdbc.queryForObject(query, String.class, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected <R> List<R> findMany(String query, RowMapper<R> mapper, Object... params) {
        try {
            return jdbc.query(query, mapper, params);
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());
        }
    }

    protected List<Long> findManyId(String query, Object... params) {
        return jdbc.queryForList(query, Long.class, params);
    }

    protected long[] insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            int rowsAffected = jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (int idx = 0; idx < params.length; idx++) {
                    ps.setObject(idx + 1, params[idx]);
                }
                return ps;
            }, keyHolder);
            List<Map<String, Object>> keyList = keyHolder.getKeyList();

            if (keyList != null && !keyList.isEmpty()) {
                long[] generatedKeys = new long[keyList.size()];
                for (int i = 0; i < keyList.size(); i++) {
                    Map<String, Object> keyMap = keyList.get(i);
                    Object keyValue = keyMap.values().iterator().next();
                    if (keyValue instanceof Number) {
                        generatedKeys[i] = ((Number) keyValue).longValue();
                    } else {
                        throw new InternalServerException("Сгенерированный ключ не является числом");
                    }
                }
                return generatedKeys;
            } else if (rowsAffected > 0) {
                return new long[]{rowsAffected};
            } else {
                throw new InternalServerException("Не удалось сохранить данные");
            }
        } catch (Exception e) {
            throw new InternalServerException("Не удалось сохранить данные" + e.getMessage());
        }
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
    }
}
