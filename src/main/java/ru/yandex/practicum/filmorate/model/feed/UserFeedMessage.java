package ru.yandex.practicum.filmorate.model.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.constants.FeedEventType;
import ru.yandex.practicum.filmorate.constants.FeedOperations;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedMessage {
    private Long eventId;
    private Long userId;
    private Long timestamp;
    private FeedEventType eventType;
    private FeedOperations operation;
    private Long entityId;
}