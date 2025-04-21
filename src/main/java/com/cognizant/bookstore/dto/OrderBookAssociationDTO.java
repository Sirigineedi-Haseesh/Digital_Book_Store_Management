package com.cognizant.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class OrderBookAssociationDTO {
	private Long id;

    @NotNull(message = "Order ID cannot be null")
    private Long orderId;

    @NotNull(message = "Book ID cannot be null")
    private Long bookId;
    
    @NotNull(message="Quantity is not mentioned")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}

