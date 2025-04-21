package com.cognizant.bookstore.dto;

import java.time.LocalDate;
import java.util.Set;

import com.cognizant.bookstore.model.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailsDTO {
	private Long orderId;
	@NotNull(message = "User ID cannot be null")
	private Long userId;
	private LocalDate orderDate; // Set automatically
	@NotNull(message = "Total amount cannot be null")
	private Double totalAmount;
	private Set<OrderBookAssociationDTO> orderBooks; // Allow empty orders for now
	@NotNull(message = "Order status cannot be null")
	private OrderStatus orderStatus;
}
