package ru.yandex.practicum.filmorate.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не может быть пустым")
    String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "[^\\s]+", message = "Логин не может содержать пробелы")
    String login;
    String name;
    @Past(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
    Set<Long> friends = new HashSet<>();

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
    }

    public boolean addFriendId(Long friendId) {
        return friends.add(friendId);
    }

    public boolean removeFriendId(Long friendId) {
        return friends.remove(friendId);
    }
}
