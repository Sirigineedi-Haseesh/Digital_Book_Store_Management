package com.cognizant.bookstore.dto;

import com.cognizant.bookstore.model.Book;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class InventoryDTO {

    @NotNull(message = "Inventory ID shouldn't be empty")
    private long inventoryId;

    private Book book;

    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private int stock;
}

 