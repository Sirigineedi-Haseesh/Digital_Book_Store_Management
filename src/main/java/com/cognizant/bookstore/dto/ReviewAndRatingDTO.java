package com.cognizant.bookstore.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class ReviewAndRatingDTO {
    private int reviewId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating;

    @NotBlank(message = "Review cannot be empty")
    private String review;

    private LocalDateTime date;

    @NotBlank(message = "Book title cannot be empty")
    private String bookTitle;

    @NotNull(message = "Book ID cannot be null")
    private Long bookId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

   
}
