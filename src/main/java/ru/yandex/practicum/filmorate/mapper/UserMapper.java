package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.HashSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        user.setFriends(new HashSet<>());

        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());
        dto.setFriends(user.getFriends());

        return dto;
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasId()) {
            if (request.hasEmail()) {
                user.setEmail(request.getEmail());
            }
            if (request.hasLogin()) {
                user.setLogin(request.getLogin());
            }
            if (request.hasName()) {
                user.setName(request.getName());
            }
            if (request.hasBirthday()) {
                user.setBirthday(request.getBirthday());
            }
        }

        return user;
    }
}
