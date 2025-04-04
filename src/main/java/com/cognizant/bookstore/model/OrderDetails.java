package com.cognizant.bookstore.model;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="orders")
public class OrderDetails {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
   // @ManyToOne
    //private User userId;
    //@ManyToOne
    //private Book bookId;
    private Date orderDate; 
//    private OrderDetails orderDetails;
    private Double totalAmount;
}
