package ru.yandex.practicum.filmorate.storage.feed.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constants.FeedEventType;
import ru.yandex.practicum.filmorate.constants.FeedOperations;
import ru.yandex.practicum.filmorate.model.feed.UserFeedMessage;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class FeedRowMapper implements RowMapper<UserFeedMessage> {

    @Override
    public UserFeedMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserFeedMessage event = new UserFeedMessage();
        event.setEventId(rs.getLong("id"));
        event.setUserId(rs.getLong("user_id"));
        event.setTimestamp(rs.getTimestamp("timestamp").toInstant().toEpochMilli());

        event.setEventType(FeedEventType.valueOf(rs.getString("event_type")));
        event.setOperation(FeedOperations.valueOf(rs.getString("operation")));

        event.setEntityId(rs.getLong("entity_id"));

        return event;
    }
}