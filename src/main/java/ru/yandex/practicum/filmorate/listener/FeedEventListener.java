package ru.yandex.practicum.filmorate.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.storage.feed.FeedDbStorage;

@Component
public class FeedEventListener implements ApplicationListener<UserFeedEvent> {
    private final FeedDbStorage feedDbStorage;

    @Autowired
    public FeedEventListener(FeedDbStorage feedDbStorage) {
        this.feedDbStorage = feedDbStorage;
    }

    @Override
    @Transactional
    public void onApplicationEvent(UserFeedEvent event) {
        feedDbStorage.addEvent(
                event.getUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEntityId()
        );
    }
}