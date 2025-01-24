package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "[^\\s]+", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
