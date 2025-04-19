package com.cognizant.bookstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
//@Data
@Getter
@Setter
@ToString
@Table(name = "order_book_association")
public class OrderBookAssociation {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    @ToString.Exclude
    private OrderDetails order;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    @ToString.Exclude
    private Book book;

    private int quantity; // Tracks how many copies of each book were ordered
}
