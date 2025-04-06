package com.cognizant.bookstore.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ReviewAndRatingDTO {
    private int reviewId;
    private int rating;
    private String review;
    private Date date;
    private String bookTitle;
    private String userName;
}
