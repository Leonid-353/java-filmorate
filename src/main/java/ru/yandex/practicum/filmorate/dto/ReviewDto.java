package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long reviewId;
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Long useful;
}
