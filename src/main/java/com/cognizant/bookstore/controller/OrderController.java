package com.cognizant.bookstore.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.exceptions.*;
import com.cognizant.bookstore.service.OrderDetailsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderController {
	
    @Autowired
    private OrderDetailsService orderDetailsService;
    
    @GetMapping("/admin/date/{date}")
    public ResponseEntity<?> getOrderDetailsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Received request to fetch order details for date: {}", date);

        try {
            List<OrderDetailsDTO> orderDetails = orderDetailsService.getOrderDetailsByDate(date);
            log.info("Successfully fetched {} order(s) for date: {}", orderDetails.size(), date);
            return ResponseEntity.ok(orderDetails);
        } catch (NoOrderDetailsFoundException e) {
            log.warn("No orders found for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching orders for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/admin/dateRange")
    public ResponseEntity<?> getOrderDetailsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Received request to fetch order details between dates: {} and {}", startDate, endDate);

        try {
            List<OrderDetailsDTO> orderDetails = orderDetailsService.getOrderDetailsByDateRange(startDate, endDate);
            log.info("Successfully fetched {} order(s) between dates: {} and {}", orderDetails.size(), startDate, endDate);
            return ResponseEntity.ok(orderDetails);
        } catch (NoOrderDetailsFoundException e) {
            log.warn("No orders found between dates: {} and {}", startDate, endDate, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid date range provided: {} to {}", startDate, endDate, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching orders between dates: {} and {}", startDate, endDate, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDetailsDTO orderDetailsDTO) {
        log.info("Received request to create an order for user ID: {}", orderDetailsDTO.getUserId());

        try {
            OrderDetailsDTO savedOrder = orderDetailsService.createOrder(orderDetailsDTO);
            log.info("Order created successfully for user ID: {} with total amount: {}", 
                    orderDetailsDTO.getUserId(), savedOrder.getTotalAmount());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
        } catch (UserNotFoundException e) {
            log.warn("User not found for order creation. User ID: {}", orderDetailsDTO.getUserId(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BooksNotFoundException e) {
            log.warn("A book in the order was not found. User ID: {}", orderDetailsDTO.getUserId(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidOrderException e) {
            log.warn("Invalid order details provided for user ID: {}", orderDetailsDTO.getUserId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating an order for user ID: {}", 
                    orderDetailsDTO.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    @PostMapping("/admin/changeStatus/{id}/{status}")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @PathVariable String status) {
        log.info("Received request to change status of order ID: {} to {}", id, status);

        try {
            String result = orderDetailsService.changeOrderStatus(id, status);
            log.info("Successfully changed status of order ID: {} to {}", id, status);
            return ResponseEntity.ok(result);
        } catch (OrderNotFoundException e) {
            log.warn("Order not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided for order ID: {}. Status: {}", id, status, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while changing status of order ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
