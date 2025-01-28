package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAllUsers();

    Optional<User> findUser(Long userId);

    User createUser(User user);

    User updateUser(User newUser);

    void removeUser(Long userId);

}
