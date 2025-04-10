package com.cognizant.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.bookstore.model.OrderDetails;

public interface OrderRepository extends JpaRepository<OrderDetails,Long> {
	

}
