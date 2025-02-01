package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // CRUD
    @GetMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto findReview(@PathVariable("reviewId") Long reviewId) {
        return reviewService.findReviewDto(reviewId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ReviewDto> findAllReviews(@RequestParam(required = false)
                                                @Min(value = 1) Long filmId,
                                                @RequestParam(defaultValue = "10")
                                                @Min(value = 1) int count) {
        return reviewService.findAllReviewsDto(filmId, count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto findReview(@Valid @RequestBody NewReviewRequest newReviewRequest) {
        log.info("Received create request body: {}", newReviewRequest);
        return reviewService.createReview(newReviewRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto updateReview(@Valid @RequestBody UpdateReviewRequest updateReviewRequest) {
        log.info("Received update request body: {}", updateReviewRequest);
        return reviewService.updateReview(updateReviewRequest);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.removeReview(reviewId);
    }

    // Like/Dislike
    @PutMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void likeIt(@PathVariable("reviewId") Long reviewId,
                       @PathVariable("userId") Long userId) {
        reviewService.likeIt(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void dislikeIt(@PathVariable("reviewId") Long reviewId,
                          @PathVariable("userId") Long userId) {
        reviewService.dislikeIt(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable("reviewId") Long reviewId,
                           @PathVariable("userId") Long userId) {
        reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDislike(@PathVariable("reviewId") Long reviewId,
                              @PathVariable("userId") Long userId) {
        reviewService.removeDislike(reviewId, userId);
    }
}
