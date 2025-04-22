package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.ReviewAndRatingDTO;
import com.cognizant.bookstore.model.ReviewAndRating;

import java.util.List;

public interface ReviewAndRatingService {

    ReviewAndRatingDTO addReviewAndRating(ReviewAndRatingDTO dto);

    List<ReviewAndRatingDTO> getAllReviewsAndRatings();

    ReviewAndRatingDTO getReviewAndRatingById(int id);

    ReviewAndRatingDTO updateReviewAndRating(ReviewAndRatingDTO dto);

    void deleteReviewAndRating(int id);

    ReviewAndRatingDTO updateReviewAndRatingFields(int id, ReviewAndRatingDTO dto);

	List<ReviewAndRating> getReviewsByBookTitle(String bookTitle);
}
