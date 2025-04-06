package com.cognizant.bookstore.service;

import java.util.List;
import java.util.stream.Collectors;

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

    // Add a new review and rating
    public ReviewAndRatingDTO addReviewAndRating(ReviewAndRatingDTO dto) {
        ReviewAndRating reviewAndRating = convertDTOToEntity(dto);
        ReviewAndRating savedEntity = reviewAndRatingRepository.save(reviewAndRating);
        return convertEntityToDTO(savedEntity);
    }

    // Get all reviews and ratings
    public List<ReviewAndRatingDTO> getAllReviewsAndRatings() {
        List<ReviewAndRating> reviews = reviewAndRatingRepository.findAll();
        return reviews.stream()
                      .map(this::convertEntityToDTO)
                      .collect(Collectors.toList());
    }

    // Get a single review and rating by ID
    public ReviewAndRatingDTO getReviewAndRatingById(int id) {
        ReviewAndRating reviewAndRating = reviewAndRatingRepository.findById(id).orElse(null);
        if (reviewAndRating == null) {
            throw new IllegalArgumentException("Review with ID " + id + " not found");
        }
        return convertEntityToDTO(reviewAndRating);
    }

    // Update an existing review and rating
    public ReviewAndRatingDTO updateReviewAndRating(ReviewAndRatingDTO dto) {
        if (!reviewAndRatingRepository.existsById(dto.getReviewId())) {
            throw new IllegalArgumentException("Review with ID " + dto.getReviewId() + " not found");
        }
        ReviewAndRating reviewAndRating = convertDTOToEntity(dto);
        ReviewAndRating updatedEntity = reviewAndRatingRepository.save(reviewAndRating);
        return convertEntityToDTO(updatedEntity);
    }

    // Delete a review and rating by ID
    public void deleteReviewAndRating(int id) {
        if (!reviewAndRatingRepository.existsById(id)) {
            throw new IllegalArgumentException("Review with ID " + id + " not found");
        }
        reviewAndRatingRepository.deleteById(id);
    }

    // Convert Entity to DTO
    private ReviewAndRatingDTO convertEntityToDTO(ReviewAndRating reviewAndRating) {
        ReviewAndRatingDTO dto = new ReviewAndRatingDTO();
        dto.setReviewId(reviewAndRating.getReviewId());
        dto.setRating(reviewAndRating.getRating());
        dto.setReview(reviewAndRating.getReview());
        dto.setDate(reviewAndRating.getDate());
        dto.setBookTitle(reviewAndRating.getBook().getTitle()); // Example: Book title
        dto.setUserName(reviewAndRating.getUser().getName());   // Example: User name
        return dto;
    }

    // Convert DTO to Entity
    private ReviewAndRating convertDTOToEntity(ReviewAndRatingDTO dto) {
        ReviewAndRating reviewAndRating = new ReviewAndRating();
        reviewAndRating.setReviewId(dto.getReviewId());
        reviewAndRating.setRating(dto.getRating());
        reviewAndRating.setReview(dto.getReview());
        reviewAndRating.setDate(dto.getDate());

        // Fetch Book entity from database
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(
            () -> new IllegalArgumentException("Book with ID " + dto.getBookId() + " not found"));
        reviewAndRating.setBook(book);

        // Fetch User entity from database
        User user = userRepository.findById(dto.getUserId()).orElseThrow(
            () -> new IllegalArgumentException("User with ID " + dto.getUserId() + " not found"));
        reviewAndRating.setUser(user);

        return reviewAndRating;
    }
}
