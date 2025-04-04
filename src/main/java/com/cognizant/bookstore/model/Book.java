package com.cognizant.bookstore.model;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookId;
    private String isbn;
    private String title;
    private String category;
    private long price;
    private String authorName;
    private String images;
    @ManyToMany(mappedBy = "books")
    private Set<User> users = new HashSet<>();
    @OneToOne(mappedBy="book")
    private Inventory inventory;
}
