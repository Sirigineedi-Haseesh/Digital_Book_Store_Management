package com.cognizant.bookstore.dto;
import com.cognizant.bookstore.model.Inventory;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

@Data
public class BookDTO {
    private long bookId;

    @NotBlank(message = "ISBN cannot be empty")
    private String isbn;

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Category cannot be empty")
    private String category;

    @Positive(message = "Price must be a positive value")
    private long price;

    @NotBlank(message = "Author name cannot be empty")
    private String authorName;

    private String images;

    private Inventory inventory;  // No validation needed here
}


