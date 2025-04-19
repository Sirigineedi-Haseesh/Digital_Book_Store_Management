package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cognizant.bookstore.dto.ReviewAndRatingDTO;
import com.cognizant.bookstore.service.ReviewAndRatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reviews-and-ratings")
public class ReviewAndRatingController {

    @Autowired
    private ReviewAndRatingService service;

    // Add a new review and rating
    @PostMapping("/user/addReviewRating")
    public ResponseEntity<ReviewAndRatingDTO> addReviewAndRating(@Valid @RequestBody ReviewAndRatingDTO dto) {
        ReviewAndRatingDTO savedReview = service.addReviewAndRating(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }

    // Get all reviews and ratings
    @GetMapping("/admin/getallReviewandRating")
    public ResponseEntity<List<ReviewAndRatingDTO>> getAllReviewsAndRatings() {
        List<ReviewAndRatingDTO> reviews = service.getAllReviewsAndRatings();
        return reviews.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) :
                                   ResponseEntity.ok(reviews);
    }

    // Get a single review and rating by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReviewAndRatingDTO> getReviewAndRatingById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.getReviewAndRatingById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update an existing review and rating
    @PutMapping("/user/updateReview")
    public ResponseEntity<ReviewAndRatingDTO> updateReviewAndRating(@Valid @RequestBody ReviewAndRatingDTO dto) {
        try {
            return ResponseEntity.ok(service.updateReviewAndRating(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Delete a review and rating by ID
    @DeleteMapping("/admin/deleteRating/{id}")
    public ResponseEntity<String> deleteReviewAndRating(@PathVariable int id) {
        try {
            service.deleteReviewAndRating(id);
            return ResponseEntity.ok("Review and rating deleted successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found!");
        }
    }

    // Partially update a review
    @PatchMapping("/patchReview/{id}")
    public ResponseEntity<ReviewAndRatingDTO> patchReviewAndRating(@PathVariable int id, @Valid @RequestBody ReviewAndRatingDTO dto) {
        try {
            return ResponseEntity.ok(service.updateReviewAndRatingFields(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}