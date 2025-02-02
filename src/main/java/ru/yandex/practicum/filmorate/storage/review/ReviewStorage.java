package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.review.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> findReview(Long reviewId);

    Collection<Review> findAllReviews(int count);

    Collection<Review> findAllReviewsByFilmId(Long filmId, int count);

    Review createReview(Review review);

    Review updateReview(Review newReview);

    void removeReview(Long reviewId);
}
