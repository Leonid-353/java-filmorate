package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.constants.FeedEventType;
import ru.yandex.practicum.filmorate.constants.FeedOperations;
import ru.yandex.practicum.filmorate.model.feed.UserFeedMessage;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.feed.mapper.FeedRowMapper;

import java.util.Collection;

@Repository
public class FeedDbStorage extends BaseDbStorage<UserFeedMessage> implements FeedStorage {
    private static final String ADD_EVENT_TO_USER_HISTORY = "INSERT INTO user_feed(user_id, event_type, operation, entity_id) VALUES (?,?,?,?)";
    private static final String GET_EVENTS_BY_USER_ID = "SELECT * FROM user_feed WHERE user_id = ?";
    private static final String DELETE_EVENTS_BY_USER_ID = "DELETE FROM user_feed WHERE user_id = ?";
    private static final String DELETE_EVENTS_FOR_ENTITIES = "DELETE FROM user_feed WHERE entity_id = ? AND event_type = ?";

    public FeedDbStorage(JdbcTemplate jdbc, FeedRowMapper mapper) {
        super(jdbc, mapper, UserFeedMessage.class);
    }

    public void addEvent(Long userId, FeedEventType eventType, FeedOperations operation, Long entityId) {
        insert(ADD_EVENT_TO_USER_HISTORY, userId, eventType.name(), operation.name(), entityId);
    }

    public Collection<UserFeedMessage> getEvents(Long userId) {
        return findMany(GET_EVENTS_BY_USER_ID, new FeedRowMapper(), userId);
    }

    public void deleteEvents(Long userId) {
        update(DELETE_EVENTS_BY_USER_ID, userId);
    }

    public void deleteEventsForEntities(Long entityId, FeedEventType eventType) {
        update(DELETE_EVENTS_FOR_ENTITIES, entityId, eventType);
    }
}