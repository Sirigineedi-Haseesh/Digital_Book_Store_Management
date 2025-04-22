package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cognizant.bookstore.dto.ReviewAndRatingDTO;
import com.cognizant.bookstore.exceptions.*;
import com.cognizant.bookstore.model.ReviewAndRating;
import com.cognizant.bookstore.service.ReviewAndRatingService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reviews-and-ratings")
@Slf4j
public class ReviewAndRatingController {

    @Autowired
    private ReviewAndRatingService service;

    // Add a new review and rating
    @PostMapping("/user/addReviewRating")
    public ResponseEntity<?> addReviewAndRating(@Valid @RequestBody ReviewAndRatingDTO dto) {
        log.info("Received request to add a review for book ID: {} by user ID: {}", dto.getBookId(), dto.getUserId());

        try {
            ReviewAndRatingDTO savedReview = service.addReviewAndRating(dto);
            log.info("Successfully added review with ID: {}", savedReview.getReviewId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to add review due to invalid input: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while adding review: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Get all reviews and ratings
    @GetMapping("/admin/getallReviewandRating")
    public ResponseEntity<?> getAllReviewsAndRatings() {
        log.info("Received request to fetch all reviews and ratings.");

        try {
            List<ReviewAndRatingDTO> reviews = service.getAllReviewsAndRatings();
            if (reviews.isEmpty()) {
                log.info("No reviews found in the database.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            log.info("Successfully fetched {} review(s).", reviews.size());
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching reviews: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Get a single review and rating by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewAndRatingById(@PathVariable int id) {
        log.info("Received request to fetch review with ID: {}", id);

        try {
            ReviewAndRatingDTO review = service.getReviewAndRatingById(id);
            log.info("Successfully fetched review with ID: {}", id);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            log.warn("Review with ID: {} not found. Error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching review with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Update an existing review and rating
    @PutMapping("/user/updateReview")
    public ResponseEntity<?> updateReviewAndRating(@Valid @RequestBody ReviewAndRatingDTO dto) {
        log.info("Received request to update review with ID: {}", dto.getReviewId());

        try {
            ReviewAndRatingDTO updatedReview = service.updateReviewAndRating(dto);
            log.info("Successfully updated review with ID: {}", dto.getReviewId());
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update review with ID: {}. Error: {}", dto.getReviewId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating review with ID: {}", dto.getReviewId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Delete a review and rating by ID
    @DeleteMapping("/admin/deleteRating/{id}")
    public ResponseEntity<String> deleteReviewAndRating(@PathVariable int id) {
        log.info("Received request to delete review with ID: {}", id);
        try {
            service.deleteReviewAndRating(id);
            log.info("Successfully deleted review with ID: {}", id);
            return ResponseEntity.ok("Review and rating deleted successfully!");
        } catch (IllegalArgumentException e) {
            log.warn("Failed to delete review with ID: {}. Error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found!");
        }
    }

    // Partially update a review
    @PatchMapping("/patchReview/{id}")
    public ResponseEntity<?> patchReviewAndRating(@PathVariable int id,@RequestBody ReviewAndRatingDTO dto) {
        log.info("Received request to partially update review with ID: {}", id);
        try {
            ReviewAndRatingDTO updatedReview = service.updateReviewAndRatingFields(id, dto);
            log.info("Successfully patched review fields for ID: {}", id);
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to patch review with ID: {}. Error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get reviews for a book by its title
    @GetMapping("/book/{title}")
    public ResponseEntity<?> getReviewsByBookTitle(@PathVariable String title) {
        log.info("Received request to fetch reviews for book with title: {}", title);
        try {
            List<ReviewAndRating> reviews = service.getReviewsByBookTitle(title);
            log.info("Successfully fetched {} review(s) for book with title: {}", reviews.size(), title);
            return ResponseEntity.ok(reviews);
        } catch (BookNotFoundException e) {
            log.warn("Book not found with title: {}. Error: {}", title, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (ReviewNotFoundException e) {
            log.warn("No reviews found for book with title: {}. Error: {}", title, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching reviews for book with title: {}. Error: {}", title, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Something went wrong\"}");
        }
    }
}