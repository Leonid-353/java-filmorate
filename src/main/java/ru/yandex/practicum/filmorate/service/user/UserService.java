package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
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
        } else if (!user.getFriends().contains(friend.getId()) && friend.getFriends().contains(user.getId())) {
            user.addFriendId(friend.getId());
            userDbStorage.addFriendRequest(user.getId(), friend.getId());
            userDbStorage.confirmationFriend(user.getId(), friend.getId());
            userDbStorage.confirmationFriend(friend.getId(), user.getId());
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
        }
    }
}
