package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * User.
 */
@Data
public class User {
    private Long id;
    @Email(message = "Неверный формат email")
    @NotNull
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "[^\\s]+", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
    }
}
