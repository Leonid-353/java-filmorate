package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.constants.FeedEventType;
import ru.yandex.practicum.filmorate.constants.FeedOperations;
import ru.yandex.practicum.filmorate.model.feed.UserFeedMessage;

import java.util.Collection;

public interface FeedStorage {
    void addEvent(Long userId, FeedEventType eventType, FeedOperations operation, Long entityId);

    Collection<UserFeedMessage> getEvents(Long userId);

    void deleteEvents(Long userId);
}