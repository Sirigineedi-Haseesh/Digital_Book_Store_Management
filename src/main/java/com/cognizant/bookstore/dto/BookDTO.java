package com.cognizant.bookstore.dto;
import lombok.Getter;
import lombok.Setter;

import com.cognizant.bookstore.model.Inventory;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
public class BookDTO {
	
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
    
    @NotNull(message = "Stock must be provided")
    @Valid
    private InventoryDTO inventory;  // No validation needed here
}


