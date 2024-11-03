package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAllUsers();

    User findUser(Long userId);

    User createUser(User user);

    User updateUser(User newUser);

    void removeUser(Long userId);
}
