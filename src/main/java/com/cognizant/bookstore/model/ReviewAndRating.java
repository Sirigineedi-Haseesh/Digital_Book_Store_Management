package com.cognizant.bookstore.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
@Entity
public class ReviewAndRating {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int reviewId;
	private int rating;
	@ManyToOne //from book table
	private Book book;
	private String review;
	private LocalDateTime date;
	@ManyToOne
	@JsonIgnore
	private User user; //from user table
}
