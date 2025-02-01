package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewReviewRequest {
    @NotBlank
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Long userId;
    @NotNull
    Long filmId;
}
