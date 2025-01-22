package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserRowMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
//@Qualifier("userDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_ALL_FRIEND = "SELECT friend_id FROM friend_request WHERE user_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_FRIENDS = "DELETE FROM friend_request WHERE user_id = ?";
    private static final String DELETE_BY_ID_FRIEND = "DELETE FROM friend_request " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String ADD_FRIEND_REQUEST = "INSERT INTO friend_request(id, user_id, friend_id, is_confirmed)" +
            "VALUES (?, ?, ?, ?)";
    private static final String CONFIRMATION_FRIEND_REQUEST = "UPDATE friend_request SET is_confirmed = true " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_BY_ID_FRIEND_REQUEST = "SELECT user_id FROM friend_request " +
            "WHERE friend_id = ? AND is_confirmed = false";

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public Collection<User> findAllUsers() {
        List<User> users = findMany(FIND_ALL_QUERY);
        users.forEach(user -> user.setFriends(new HashSet<>(findManyId(FIND_ALL_FRIEND, user.getId()))));
        return users;
    }

    @Override
    public Optional<User> findUser(Long userId) {
        return findOne(FIND_BY_ID_QUERY, userId)
                .map(user -> {
                    user.setFriends(new HashSet<>(findManyId(FIND_ALL_FRIEND, user.getId())));
                    return user;
                });
    }

    @Override
    public User createUser(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        update(
                UPDATE_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday()
        );
        return newUser;
    }

    @Override
    public void removeUser(Long userId) {
        delete(DELETE_FRIENDS, userId);
        delete(DELETE_QUERY, userId);
    }

    //@Override
    public void addFriendRequest(Long userId, Long friendId) {
        Long id = insert(
                ADD_FRIEND_REQUEST,
                userId,
                friendId,
                false
        );
    }

    //@Override
    public void confirmationFriend(Long userId, Long friendId) {
        update(
                CONFIRMATION_FRIEND_REQUEST,
                userId,
                friendId
        );
    }

    //@Override
    public Collection<Long> findFriendRequests(Long userId) {
        return findManyId(FIND_BY_ID_FRIEND_REQUEST, userId);
    }

    //@Override
    public void unfriend(Long userId, Long friendId) {
        delete(DELETE_BY_ID_FRIEND, userId, friendId);
    }
}
