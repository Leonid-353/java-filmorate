package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.review.mapper.ReviewRowMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String FIND_ALL_BY_FILM_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ? " +
            "ORDER BY useful DESC " +
            "LIMIT ?";
    private static final String FIND_ALL_LIKES = "SELECT user_id FROM review_likes_dislikes " +
            "WHERE review_id = ? AND like_dislike = ?";
    private static final String FIND_ALL_DISLIKES = "SELECT user_id FROM review_likes_dislikes " +
            "WHERE review_id = ? AND like_dislike = ?";
    private static final String INSERT_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_LIKE_DISLIKE_QUERY = "INSERT INTO review_likes_dislikes " +
            "(review_id, user_id, like_dislike) " +
            "VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE reviews " +
            "SET content = ?, is_positive = ?, user_id = ?, film_id = ? " +
            "WHERE id = ?";
    private static final String UPDATE_USEFUL_QUERY = "UPDATE reviews " +
            "SET useful = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE id = ?";
    private static final String DELETE_LIKES_DISLIKES_QUERY = "DELETE FROM review_likes_dislikes WHERE review_id = ?";
    private static final String DELETE_LIKE_OR_DISLIKE = "DELETE FROM review_likes_dislikes " +
            "WHERE review_id = ? AND user_id = ?";

    public ReviewDbStorage(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc, mapper, Review.class);
    }

    @Override
    public Optional<Review> findReview(Long reviewId) {
        return countUseful(findOne(FIND_BY_ID_QUERY, new ReviewRowMapper(), reviewId));
    }

    @Override
    public Collection<Review> findAllReviews(int count) {
        return findMany(FIND_ALL_QUERY, new ReviewRowMapper(), count).stream()
                .peek(review -> countUseful(Optional.ofNullable(review)))
                .toList();
    }

    @Override
    public Collection<Review> findAllReviewsByFilmId(Long filmId, int count) {
        return findMany(FIND_ALL_BY_FILM_ID_QUERY, new ReviewRowMapper(), filmId, count).stream()
                .peek(review -> countUseful(Optional.ofNullable(review)))
                .toList();
    }

    @Override
    public Review createReview(Review review) {
        long id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        )[0];
        review.setId(id);

        return review;
    }

    @Override
    public Review updateReview(Review newReview) {
        update(
                UPDATE_QUERY,
                newReview.getContent(),
                newReview.isPositive(),
                newReview.getUserId(),
                newReview.getFilmId(),
                newReview.getId()
        );
        return newReview;
    }

    @Override
    public void removeReview(Long reviewId) {
        update(
                DELETE_LIKES_DISLIKES_QUERY,
                reviewId

        );
        update(
                DELETE_QUERY,
                reviewId
        );
    }

    public void likeIt(Long reviewId, Long userId) {
        update(
                DELETE_LIKE_OR_DISLIKE,
                reviewId,
                userId
        );
        insert(
                INSERT_LIKE_DISLIKE_QUERY,
                reviewId,
                userId,
                true
        );
    }

    public void dislikeIt(Long reviewId, Long userId) {
        update(
                DELETE_LIKE_OR_DISLIKE,
                reviewId,
                userId
        );
        insert(
                INSERT_LIKE_DISLIKE_QUERY,
                reviewId,
                userId,
                false
        );
    }

    public void removeLikeDislike(Long reviewId, Long userId) {
        update(
                DELETE_LIKE_OR_DISLIKE,
                reviewId,
                userId
        );
    }

    private Optional<Review> countUseful(Optional<Review> reviewOpt) {
        return reviewOpt.map(review -> {
                    review.setLikes(new HashSet<>(findManyId(FIND_ALL_LIKES, review.getId(), true)));
                    return review;
                })
                .map(review -> {
                    review.setDislikes(new HashSet<>(findManyId(FIND_ALL_DISLIKES, review.getId(), false)));
                    return review;
                })
                .map(review -> {
                    int count = review.getLikes().size() - review.getDislikes().size();
                    review.setUseful((long) count);
                    update(
                            UPDATE_USEFUL_QUERY,
                            review.getUseful(),
                            review.getId()
                    );
                    return review;
                });
    }
}
