package com.cognizant.bookstore.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class ReviewAndRatingDTO {
    private int reviewId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating;

    @NotBlank(message = "Review cannot be empty")
    private String review;

    private Date date;

    @NotBlank(message = "Book title cannot be empty")
    private String bookTitle;

    @NotBlank(message = "Username cannot be empty")
    private String userName;
}
