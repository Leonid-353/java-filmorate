package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
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
        log.debug("Для пользователя устновлен id: {}", user.getId());
        user.setName(user.getName());
        log.trace("Для пользователя установлено поле name: {}", user.getName());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            log.warn("Id пользователя не указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            log.trace("Пользователь найден в хранилище");
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
                log.trace("Обновление данных о пользователе");
                oldUser.setEmail(newUser.getEmail());
                oldUser.setLogin(newUser.getLogin());
                oldUser.setName(newUser.getName());
                oldUser.setBirthday(newUser.getBirthday());
                log.info("Данные о пользователе обновлены");
                return oldUser;
            } else {
                log.info("Данные о пользователе обновлять не требуется");
                return oldUser;
            }
        }
        log.warn("Пользователь не найден в хранилище");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
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
