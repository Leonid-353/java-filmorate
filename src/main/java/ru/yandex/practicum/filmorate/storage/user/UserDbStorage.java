package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_ALL_FRIEND = "SELECT friend_id FROM friend_request WHERE user_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_USER_ID_FRIEND_REQUEST = "SELECT user_id FROM friend_request " +
            "WHERE friend_id = ? AND is_confirmed = false";
    private static final String FIND_USER_ID_LIKES = "SELECT user_id FROM likes " +
            "WHERE user_id = ?";
    private static final String FIND_USER_ID_FRIEND_REQUEST_BY_USER_ID_ONLY = "SELECT user_id FROM friend_request " +
            "WHERE user_id = ?";
    private static final String FIND_FRIEND_ID_FRIEND_REQUEST = "SELECT user_id FROM friend_request " +
            "WHERE friend_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(login, name, email, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_FRIENDS = "DELETE FROM friend_request WHERE user_id = ?";
    private static final String DELETE_FRIENDS_BY_FRIEND_ID = "DELETE FROM friend_request WHERE friend_id = ?";
    private static final String DELETE_BY_ID_FRIEND = "DELETE FROM friend_request " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_Like_QUERY = "DELETE FROM likes WHERE user_id = ?";
    private static final String ADD_FRIEND_REQUEST = "INSERT INTO friend_request(user_id, friend_id, is_confirmed)" +
            "VALUES (?, ?, ?)";
    private static final String CONFIRMATION_FRIEND_REQUEST = "UPDATE friend_request SET is_confirmed = true " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_BY_ID_FRIEND_REQUEST = "SELECT user_id FROM friend_request " +
            "WHERE friend_id = ? AND is_confirmed = false";
    private static final String FIND_USER_BY_FILMS_LIKE = "SELECT * FROM users LEFT JOIN likes ON users.id = likes.user_id where likes.film_id IN (%s)";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public Collection<User> findAllUsers() {
        List<User> users = findMany(FIND_ALL_QUERY, new UserRowMapper());
        users.forEach(user -> user.setFriends(new HashSet<>(findManyId(FIND_ALL_FRIEND, user.getId()))));
        return users;
    }

    @Override
    public Optional<User> findUser(Long userId) {
        return findOne(FIND_BY_ID_QUERY, new UserRowMapper(), userId)
                .map(user -> {
                    List<Long> ids = findManyId(FIND_ALL_FRIEND, userId);
                    System.out.println(ids);
                    user.setFriends(new HashSet<>(ids));
                    return user;
                });
    }

    @Override
    public User createUser(User user) {
        Long id = insert(
                INSERT_QUERY,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday()
        )[0];
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        update(
                UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public void removeUser(Long userId) {
        update(DELETE_Like_QUERY, userId);
        update(DELETE_FRIENDS, userId);
        update(DELETE_FRIENDS_BY_FRIEND_ID, userId);
        update(DELETE_QUERY, userId);
    }

    public void addFriendRequest(Long userId, Long friendId) {
        insert(
                ADD_FRIEND_REQUEST,
                userId,
                friendId,
                false
        );
    }

    public void confirmationFriend(Long userId, Long friendId) {
        update(
                CONFIRMATION_FRIEND_REQUEST,
                userId,
                friendId
        );

    }

    public Collection<Long> findFriendRequests(Long userId) {
        return findManyId(FIND_USER_ID_FRIEND_REQUEST, userId);
    }

    public void unfriend(Long userId, Long friendId) {
        update(DELETE_BY_ID_FRIEND, userId, friendId);
    }

    public Collection<User> findUsersByFilmsLike(Collection<Film> films) {

        Set<Integer> filmIds = films.stream()
                .map(film -> film.getId().intValue())
                .collect(Collectors.toSet());

        String inClause = filmIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String query = String.format(FIND_USER_BY_FILMS_LIKE, inClause);

        return findMany(query, new UserRowMapper(), filmIds.toArray());
    }
}
