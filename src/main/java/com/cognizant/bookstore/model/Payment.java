package com.cognizant.bookstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Entity
@Getter
@Setter
@Table(name="payments")
public class Payment {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private Double amount;
    private String paymentMethod;
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    @ToString.Exclude
    private OrderDetails order;
}
