package com.cognizant.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

@Data
public class OrderDetailsDTO {
    private Long orderId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private LocalDateTime orderDate;  // Set automatically

    @NotNull(message = "Total amount cannot be null")
    private Double totalAmount;

    private Set<OrderBookAssociationDTO> orderBooks; // Allow empty orders for now
}

