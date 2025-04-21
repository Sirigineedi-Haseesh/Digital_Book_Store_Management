package com.cognizant.bookstore.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.dto.ReviewAndRatingDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.ReviewAndRating;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.ReviewAndRatingRepository;
import com.cognizant.bookstore.repository.UserRepository;

@Service
public class ReviewAndRatingService {

    @Autowired
    private ReviewAndRatingRepository reviewAndRatingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Add a new review and rating
    public ReviewAndRatingDTO addReviewAndRating(ReviewAndRatingDTO dto) {
        ReviewAndRating reviewAndRating = modelMapper.map(dto, ReviewAndRating.class);

        // Set current time for the `date` field
        reviewAndRating.setDate(LocalDateTime.now());

        // Fetch Book and User from the database before saving
        reviewAndRating.setBook(bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + dto.getBookId() + " not found")));
        reviewAndRating.setUser(userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + dto.getUserId() + " not found")));

        ReviewAndRating savedEntity = reviewAndRatingRepository.save(reviewAndRating);
        return modelMapper.map(savedEntity, ReviewAndRatingDTO.class);
    }

    // Get all reviews and ratings
    public List<ReviewAndRatingDTO> getAllReviewsAndRatings() {
        return reviewAndRatingRepository.findAll()
                .stream()
                .map(review -> modelMapper.map(review, ReviewAndRatingDTO.class))
                .collect(Collectors.toList());
    }

    // Get a single review and rating by ID
    public ReviewAndRatingDTO getReviewAndRatingById(int id) {
        ReviewAndRating reviewAndRating = reviewAndRatingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID " + id + " not found"));
        return modelMapper.map(reviewAndRating, ReviewAndRatingDTO.class);
    }

    // Update an existing review and rating
    public ReviewAndRatingDTO updateReviewAndRating(ReviewAndRatingDTO dto) {
        if (!reviewAndRatingRepository.existsById(dto.getReviewId())) {
            throw new IllegalArgumentException("Review with ID " + dto.getReviewId() + " not found");
        }
        ReviewAndRating reviewAndRating = modelMapper.map(dto, ReviewAndRating.class);
        reviewAndRating.setBook(bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + dto.getBookId() + " not found")));
        reviewAndRating.setUser(userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + dto.getUserId() + " not found")));

        // Preserve existing date or set a new one if necessary
        if (dto.getDate() == null) {
            reviewAndRating.setDate(LocalDateTime.now());
        }

        ReviewAndRating updatedEntity = reviewAndRatingRepository.save(reviewAndRating);
        return modelMapper.map(updatedEntity, ReviewAndRatingDTO.class);
    }

    // Delete a review and rating by ID
    public void deleteReviewAndRating(int id) {
        if (!reviewAndRatingRepository.existsById(id)) {
            throw new IllegalArgumentException("Review with ID " + id + " not found");
        }
        reviewAndRatingRepository.deleteById(id);
    }

    // Partially update a review
    public ReviewAndRatingDTO updateReviewAndRatingFields(int id, ReviewAndRatingDTO dto) {
        ReviewAndRating existingReview = reviewAndRatingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID " + id + " not found"));

        // Apply partial updates only if fields are non-null
        if (dto.getRating() != 0) {
            existingReview.setRating(dto.getRating());
        }
        if (dto.getReview() != null) {
            existingReview.setReview(dto.getReview());
        }
        if (dto.getDate() == null) {
            existingReview.setDate(LocalDateTime.now());
        }

        ReviewAndRating updatedReview = reviewAndRatingRepository.save(existingReview);
        return modelMapper.map(updatedReview, ReviewAndRatingDTO.class);
    }
}
