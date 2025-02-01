package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateReviewRequest {
    @NotNull
    Long reviewId;
    @NotBlank
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Long userId;
    @NotNull
    Long filmId;
    boolean positiveSet;

    public void setIsPositive(boolean isPositive) {
        this.isPositive = isPositive;
        this.positiveSet = true;
    }

    public boolean hasId() {
        return reviewId != null;
    }

    public boolean hasContent() {
        return !(content == null || content.isBlank());
    }

    public boolean hasPositive() {
        return positiveSet;
    }

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasFilmId() {
        return filmId != null;
    }

    /*public boolean hasUseful() {
        return useful != null;
    }*/
}
