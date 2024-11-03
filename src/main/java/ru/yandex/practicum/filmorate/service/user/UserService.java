package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
public class UserService {
    final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAllFriendsUser(Long userId) {
        return userStorage.findUser(userId).getFriends().stream()
                .map(userStorage::findUser)
                .toList();
    }

    public Collection<User> findCommonFriendsUser(Long userId, Long otherUserId) {
        Set<Long> userIdFriends = userStorage.findUser(userId).getFriends();
        Set<Long> otherUserIdFriends = userStorage.findUser(otherUserId).getFriends();
        userIdFriends.retainAll(otherUserIdFriends);
        return userIdFriends.stream()
                .map(userStorage::findUser)
                .toList();
    }

    public void addAsFriend(Long userId, Long friendId) {
        User user = userStorage.findUser(userId);
        User friend = userStorage.findUser(friendId);
        if (user.addFriendId(friend.getId())) {
            friend.addFriendId(user.getId());
        }
    }

    public void unfriend(Long userId, Long friendId) {
        User user = userStorage.findUser(userId);
        User friend = userStorage.findUser(friendId);
        if (user.removeFriendId(friend.getId())) {
            friend.removeFriendId(user.getId());
        }
    }
}
