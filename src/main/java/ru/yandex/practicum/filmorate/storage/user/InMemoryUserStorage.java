package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User findUser(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Запрос на получение. Пользователь с id: {} не найден в хранилище", userId);
            throw new NotFoundException(String.format("Пользователь с id: %d не найден в хранилище", userId));
        }
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            log.debug("Рассматривается пользователь: {}", entry.getValue().getName());
            if (entry.getValue().getEmail().equals(user.getEmail())) {
                log.warn("При добавлении указан занятый email");
                throw new DuplicatedDataException("Этот email уже используется");
            }
            if (entry.getValue().getLogin().equals(user.getLogin())) {
                log.warn("При добавлении указан занятый login");
                throw new DuplicatedDataException("Этот login уже используется");
            }
        }
        user.setId(getNextId());
        user.setName(user.getName());
        users.put(user.getId(), user);
        log.info("Пользователь id = {}, name = \"{}\" успешно добавлен", user.getId(), user.getName());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Id пользователя не указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (!oldUser.equals(newUser)) {
                for (Map.Entry<Long, User> entry : users.entrySet()) {
                    if (entry.getValue().getEmail().equals(newUser.getEmail())) {
                        log.warn("При обновлении указан занятый email");
                        throw new DuplicatedDataException("Этот email уже используется");
                    }
                    if (entry.getValue().getLogin().equals(newUser.getLogin())) {
                        log.warn("При обновлении указан занятый login");
                        throw new DuplicatedDataException("Этот login уже используется");
                    }
                }
                oldUser.setEmail(newUser.getEmail());
                oldUser.setLogin(newUser.getLogin());
                oldUser.setName(newUser.getName());
                oldUser.setBirthday(newUser.getBirthday());
                log.info("Данные о пользователе обновлены");
            } else {
                log.info("Данные о пользователе обновлять не требуется");
            }
            return oldUser;
        }
        log.warn("Пользователь не найден в хранилище");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public void removeUser(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Запрос на удаление. Пользователь с id: {} не найден в хранилище", userId);
            throw new NotFoundException(String.format("Пользователь с id: %d не найден в хранилище", userId));
        }
        users.remove(userId);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
