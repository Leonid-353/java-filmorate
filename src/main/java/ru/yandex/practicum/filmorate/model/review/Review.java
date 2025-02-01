package ru.yandex.practicum.filmorate.model.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    Long id;
    @NotBlank
    String content;
    boolean isPositive;
    @NotNull
    Long userId;
    @NotNull
    Long filmId;
    Long useful = 0L;
    Set<Long> likes = new HashSet<>();
    Set<Long> dislikes = new HashSet<>();

    public boolean addUserIdInLikes(Long userId) {
        dislikes.remove(userId);
        return likes.add(userId);
    }

    public boolean removeUserIdInLikes(Long userId) {
        return likes.remove(userId);
    }

    public boolean addUserIdInDislikes(Long userId) {
        likes.remove(userId);
        return dislikes.add(userId);
    }

    public boolean removeUserIdInDislikes(Long userId) {
        return dislikes.remove(userId);
    }
}
