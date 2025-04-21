package com.cognizant.bookstore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cognizant.bookstore.model.OrderDetails;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, Long> {
	@Query("SELECT o FROM OrderDetails o WHERE o.orderDate = :date")
    List<OrderDetails> findByOrderDate(LocalDate date);

    @Query("SELECT o FROM OrderDetails o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<OrderDetails> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
}
