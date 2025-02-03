package ru.yandex.practicum.filmorate.listener;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.yandex.practicum.filmorate.constants.FeedEventType;
import ru.yandex.practicum.filmorate.constants.FeedOperations;

@Getter
public class UserFeedEvent extends ApplicationEvent {
    private final Long userId;
    private final FeedEventType eventType;
    private final FeedOperations operation;
    private final Long entityId;

    public UserFeedEvent(Object source, Long userId, FeedEventType eventType, FeedOperations operation, Long entityId) {
        super(source);
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
