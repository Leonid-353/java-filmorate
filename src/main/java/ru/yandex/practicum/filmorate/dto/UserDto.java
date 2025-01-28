package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String email;
    private String login;
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate birthday;
    private Set<Long> friends;
}
