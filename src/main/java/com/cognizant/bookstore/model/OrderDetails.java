package com.cognizant.bookstore.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name="orders")
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @ToString.Exclude
    private User user;

    private LocalDate orderDate;
    private Double totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<OrderBookAssociation> orderBooks = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
