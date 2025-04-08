package com.cognizant.bookstore.dto;
import com.cognizant.bookstore.model.Inventory;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class BookDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookId;
    private String isbn;
    private String title;
    private String category;
    private long price;
    private String authorName;
    private String images;
    private Inventory inventory;
}

