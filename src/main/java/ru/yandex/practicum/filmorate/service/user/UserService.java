package ru.yandex.practicum.filmorate.service.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.FeedEventType;
import ru.yandex.practicum.filmorate.constants.FeedOperations;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.listener.UserFeedEvent;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.feed.UserFeedMessage;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;

@Service
public class UserService {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final FeedDbStorage feedDbStorage;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserService(UserDbStorage userDbStorage,
                       FilmDbStorage filmDbStorage,
                       FeedDbStorage feedDbStorage,
                       ApplicationEventPublisher eventPublisher) {
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
        this.feedDbStorage = feedDbStorage;
        this.eventPublisher = eventPublisher;
    }

    public Collection<UserDto> findAllUsersDto() {
        return userDbStorage.findAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .sorted(Comparator.comparing(UserDto::getId))
                .toList();
    }

    public UserDto findUserDto(Long userId) {
        return userDbStorage.findUser(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow();
    }

    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = UserMapper.mapToUser(newUserRequest);
        userDbStorage.createUser(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest updateUserRequest) {
        User updatedUser = userDbStorage.findUser(updateUserRequest.getId())
                .map(user -> UserMapper.updateUserFields(user, updateUserRequest))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userDbStorage.updateUser(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void removeUser(Long userId) {
        userDbStorage.removeUser(userId);
        feedDbStorage.deleteEvents(userId);
    }

    public Collection<User> findAllFriendsUser(Long userId) {
        Optional<User> userOpt = userDbStorage.findUser(userId);
        if (userOpt.isPresent()) {
            return userOpt.map(User::getFriends).stream()
                    .flatMap(Collection::stream)
                    .map(userDbStorage::findUser)
                    .map(Optional::orElseThrow)
                    .toList();
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    public Collection<User> findCommonFriendsUser(Long userId, Long otherUserId) {
        Set<Long> userIdFriends = userDbStorage.findUser(userId).map(User::getFriends).orElseThrow();
        Set<Long> otherUserIdFriends = userDbStorage.findUser(otherUserId).map(User::getFriends).orElseThrow();
        userIdFriends.retainAll(otherUserIdFriends);
        return userIdFriends.stream()
                .map(userDbStorage::findUser)
                .map(Optional::orElseThrow)
                .toList();
    }

    public void addAsFriend(Long userId, Long friendId) {
        User user = userDbStorage.findUser(userId).orElseThrow();
        User friend = userDbStorage.findUser(friendId).orElseThrow();
        if (!user.getFriends().contains(friend.getId()) && !friend.getFriends().contains(user.getId())) {
            user.addFriendId(friend.getId());
            userDbStorage.addFriendRequest(userId, friendId);
            eventPublisher.publishEvent(new UserFeedEvent(this, userId, FeedEventType.FRIEND, FeedOperations.ADD, friendId));
        } else if (!user.getFriends().contains(friend.getId()) && friend.getFriends().contains(user.getId())) {
            user.addFriendId(friend.getId());
            userDbStorage.addFriendRequest(user.getId(), friend.getId());
            userDbStorage.confirmationFriend(user.getId(), friend.getId());
            userDbStorage.confirmationFriend(friend.getId(), user.getId());
            eventPublisher.publishEvent(new UserFeedEvent(this, userId, FeedEventType.FRIEND, FeedOperations.UPDATE, friendId));
        } else {
            throw new DuplicatedDataException("Запрос на добавление в друзья уже отправлен");
        }
    }

    public void confirmationFriend(Long userId, Long friendId) {
        User user = userDbStorage.findUser(userId).orElseThrow();
        User friend = userDbStorage.findUser(friendId).orElseThrow();
        if (findFriendRequests(userId).contains(friendId)) {
            if (user.addFriendId(friend.getId())) {
                userDbStorage.confirmationFriend(userId, friendId);
            } else {
                throw new DuplicatedDataException("Пользователь уже в списке друзей");
            }
        } else {
            throw new NotFoundException("Запрос на добавление в друзья не найден");
        }
    }

    public Collection<Long> findFriendRequests(Long userId) {
        return userDbStorage.findFriendRequests(userId);
    }

    public void unfriend(Long userId, Long friendId) {
        User user = userDbStorage.findUser(userId).orElseThrow();
        User friend = userDbStorage.findUser(friendId).orElseThrow();
        if (user.removeFriendId(friend.getId())) {
            friend.removeFriendId(user.getId());
            userDbStorage.unfriend(userId, friendId);
            eventPublisher.publishEvent(new UserFeedEvent(this, userId, FeedEventType.FRIEND, FeedOperations.REMOVE, friendId));
        }
    }

    public Collection<FilmDto> findRecommendations(Long userId) {
        userDbStorage.findUser(userId).orElseThrow();
        Collection<Film> filmsUserLike = filmDbStorage.findFilmsLike(userId);
        Collection<User> similarUsers = userDbStorage.findUsersByFilmsLike(filmsUserLike);
        //similarUsersWeight - мапа для хранения весов пользователей;
        // Long - вес пользователя по кол-ву совпавших фильмов, чем больше совпадений тем больше вес такого пользователя
        Map<User, Long> similarUsersWeight = new HashMap<>();
        //similarUsersFilm мапа для хранения фильмов пользователей которые они лайкнули;
        Map<User, Collection<Film>> similarUsersFilm = new HashMap<>();

        //Находим вес каждого пользователя + попутно заполняем фильмы которые лайкнул пользователь
        for (User user : similarUsers) {
            Collection<Film> filmsSimilarUserLike = filmDbStorage.findFilmsLike(user.getId());
            similarUsersFilm.put(user, filmsSimilarUserLike);
            Long count = filmsSimilarUserLike.stream()
                    .filter(filmsUserLike::contains)
                    .count();
            similarUsersWeight.put(user, count);
        }

        // movieScore - мапа с фильмами которые лайкнули похожие пользователи
        Map<Film, Long> movieScore = new HashMap<>();


        for (Map.Entry<User, Long> entry : similarUsersWeight.entrySet()) {
            User user = entry.getKey();
            Long weight = entry.getValue();  //вес пользователя
            for (Film film : similarUsersFilm.get(user)) {
                if (!filmsUserLike.contains(film)) {   // Исключаем уже лайкнутые фильмы
                    movieScore.put(film, movieScore.getOrDefault(film, 0L) + weight);
                }
            }
        }

        return filmDbStorage.initializeDataFromLinkedTables(movieScore.entrySet().stream()
                        .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                        .map(Map.Entry::getKey)
                        .toList())
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public Collection<UserFeedMessage> findUserEvents(Long userId) {
        return feedDbStorage.getEvents(userId);
    }
}
