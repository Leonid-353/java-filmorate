package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.constants.FeedEventType;
import ru.yandex.practicum.filmorate.constants.FeedOperations;
import ru.yandex.practicum.filmorate.model.feed.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.feed.mapper.FeedRowMapper;

import java.util.Collection;

@Repository
public class FeedDbStorage extends BaseDbStorage<UserFeedEvent> implements FeedStorage {
    private final String ADD_EVENT = "INSERT INTO user_feed(user_id, event_type, operation, entity_id) VALUES (?,?,?,?)";
    private final String GET_EVENTS = "SELECT * FROM user_feed WHERE user_id = ?";
    private final String DELETE_EVENTS_BY_USER_ID = "DELETE FROM user_feed WHERE user_id = ?";

    public FeedDbStorage(JdbcTemplate jdbc, FeedRowMapper mapper) {
        super(jdbc, mapper, UserFeedEvent.class);
    }

    public void addEvent(Long userId, FeedEventType eventType, FeedOperations operation, Long entityId) {
        insert(ADD_EVENT, userId, eventType.name(), operation.name(), entityId);
    }

    public Collection<UserFeedEvent> getEvents(Long userId) {
        return findMany(GET_EVENTS, new FeedRowMapper(), userId);
    }

    public void deleteEvents(Long userId) {
        update(DELETE_EVENTS_BY_USER_ID, userId);
    }
}
