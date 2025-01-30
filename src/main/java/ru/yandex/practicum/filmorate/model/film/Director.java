package ru.yandex.practicum.filmorate.model.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Director {
    Long id;
    @NotBlank(message = "ФИО директора не могут быть пустыми")
    @NotNull(message = "ФИО директора не могут быть пустыми")
    String name;
}
