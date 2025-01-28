package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotNull
    private Long id;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "[^\\s]+", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public boolean hasId() {
        return id != null;
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}
