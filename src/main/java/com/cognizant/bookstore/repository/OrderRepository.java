package com.cognizant.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.bookstore.model.OrderDetails;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, Long> {
   
}
