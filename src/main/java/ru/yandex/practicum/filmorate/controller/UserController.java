package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.feed.UserFeedMessage;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAllUsers() {
        return userService.findAllUsersDto();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findUser(@PathVariable("userId") Long userId) {
        return userService.findUserDto(userId);
    }

    @GetMapping("/{userId}/feed")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserFeedMessage> getUserFeed(@PathVariable("userId") Long userId) {
        userService.findUserDto(userId);
        return userService.findUserEvents(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        return userService.createUser(newUserRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(updateUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable("userId") Long userId) {
        userService.removeUser(userId);
    }

    @GetMapping("/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findAllFriendsUser(@PathVariable("userId") Long userId) {
        return userService.findAllFriendsUser(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findCommonFriendsUser(@PathVariable("userId") Long userId,
                                                  @PathVariable("otherId") Long otherUserId) {
        return userService.findCommonFriendsUser(userId, otherUserId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addAsFriend(@PathVariable("userId") Long userId,
                            @PathVariable("friendId") Long friendId) {
        userService.addAsFriend(userId, friendId);
    }

    @PutMapping("/{userId}/friends/{friendId}/confirmation")
    @ResponseStatus(HttpStatus.OK)
    public void confirmationFriend(@PathVariable("userId") Long userId,
                                   @PathVariable("friendId") Long friendId) {
        userService.confirmationFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends-requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Long> findFriendRequests(@PathVariable("userId") Long userId) {
        return userService.findFriendRequests(userId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfriend(@PathVariable("userId") Long userId,
                         @PathVariable("friendId") Long friendId) {
        userService.unfriend(userId, friendId);
    }

    @GetMapping("/{userId}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> findRecommendations(@PathVariable("userId") Long userId) {
        return userService.findRecommendations(userId);
    }

}
