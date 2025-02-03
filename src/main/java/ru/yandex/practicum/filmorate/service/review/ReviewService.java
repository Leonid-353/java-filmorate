package ru.yandex.practicum.filmorate.service.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.FeedEventType;
import ru.yandex.practicum.filmorate.constants.FeedOperations;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.listener.UserFeedEvent;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ReviewService(ReviewDbStorage reviewDbStorage,
                         UserDbStorage userDbStorage,
                         FilmDbStorage filmDbStorage,
                         ApplicationEventPublisher eventPublisher) {
        this.reviewDbStorage = reviewDbStorage;
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
        this.eventPublisher = eventPublisher;
    }

    // Получение отзыва по id
    public ReviewDto findReviewDto(Long reviewId) {
        return reviewDbStorage.findReview(reviewId)
                .map(ReviewMapper::mapToReviewDto)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

    // Получение всех отзывов
    public Collection<ReviewDto> findAllReviewsDto(Long filmId, int count) {
        if (filmId == null) {
            return reviewDbStorage.findAllReviews(count).stream()
                    .map(ReviewMapper::mapToReviewDto)
                    .collect(Collectors.toList());
        } else {
            return reviewDbStorage.findAllReviewsByFilmId(filmId, count).stream()
                    .map(ReviewMapper::mapToReviewDto)
                    .collect(Collectors.toList());
        }
    }

    // Создание отзыва
    public ReviewDto createReview(NewReviewRequest newReviewRequest) {
        Review review = ReviewMapper.mapToReview(newReviewRequest);
        if (userDbStorage.findUser(newReviewRequest.getUserId()).isPresent() &&
                filmDbStorage.findFilm(newReviewRequest.getFilmId()).isPresent()) {
            Review createdReview = reviewDbStorage.createReview(review);
            eventPublisher.publishEvent(new UserFeedEvent(this, createdReview.getUserId(), FeedEventType.REVIEW, FeedOperations.ADD, createdReview.getId()));
            return ReviewMapper.mapToReviewDto(review);
        } else {
            throw new NotFoundException("Not found");
        }
    }

    // Обновление отзыва
    public ReviewDto updateReview(UpdateReviewRequest updateReviewRequest) {
        if (userDbStorage.findUser(updateReviewRequest.getUserId()).isPresent() &&
                filmDbStorage.findFilm(updateReviewRequest.getFilmId()).isPresent()) {
            Review updateReview = reviewDbStorage.findReview(updateReviewRequest.getReviewId())
                    .map(review -> ReviewMapper.updateReviewFields(review, updateReviewRequest))
                    .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
            reviewDbStorage.updateReview(updateReview);
            eventPublisher.publishEvent(new UserFeedEvent(this, updateReview.getUserId(), FeedEventType.REVIEW, FeedOperations.UPDATE, updateReview.getId()));
            return ReviewMapper.mapToReviewDto(updateReview);
        } else {
            throw new NotFoundException("Not found");
        }
    }

    // Удаление отзыва
    public void removeReview(Long reviewId) {
        ReviewDto review = findReviewDto(reviewId);
        reviewDbStorage.removeReview(reviewId);
        eventPublisher.publishEvent(new UserFeedEvent(this, review.getUserId(), FeedEventType.REVIEW, FeedOperations.REMOVE, reviewId));
    }

    // Поставить лайк отзыву
    public void likeIt(Long reviewId, Long userId) {
        Review rev = reviewDbStorage.findReview(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        if (rev.addUserIdInLikes(userId)) {
            reviewDbStorage.likeIt(reviewId, userId);
        } else {
            throw new DuplicatedDataException("Лайк уже установлен");
        }
    }

    // Поставить дизлайк отзыву
    public void dislikeIt(Long reviewId, Long userId) {
        Review rev = reviewDbStorage.findReview(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        if (rev.addUserIdInDislikes(userId)) {
            reviewDbStorage.dislikeIt(reviewId, userId);
        } else {
            throw new DuplicatedDataException("Дизлайк уже установлен");
        }
    }

    // Удалить лайк отзыву
    public void removeLike(Long reviewId, Long userId) {
        Review rev = reviewDbStorage.findReview(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        if (rev.removeUserIdInLikes(userId)) {
            reviewDbStorage.removeLikeDislike(reviewId, userId);
        } else {
            throw new NotFoundException("Лайк не был установлен");
        }
    }

    // Удалить дизлайк отзыву
    public void removeDislike(Long reviewId, Long userId) {
        Review rev = reviewDbStorage.findReview(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        if (rev.removeUserIdInDislikes(userId)) {
            reviewDbStorage.removeLikeDislike(reviewId, userId);
        } else {
            throw new NotFoundException("Дизлайк не был установлен");
        }
    }
}
