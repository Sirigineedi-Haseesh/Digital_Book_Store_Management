package com.cognizant.bookstore.repository;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.bookstore.model.ReviewAndRating;
 
public interface ReviewAndRatingRepository extends JpaRepository<ReviewAndRating, Integer> {

    // Add custom methods here if needed
	List<ReviewAndRating> findByBookBookId(Long bookId);

}
 