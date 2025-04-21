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

import jakarta.validation.Valid;

@RestController
public class OrderController {
	
    @Autowired
    private OrderDetailsService orderDetailsService;
    
    @GetMapping("/admin/date/{date}")
    public ResponseEntity<?> getOrderDetailsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            // Call the service method to fetch orders by date
            List<OrderDetailsDTO> orderDetails = orderDetailsService.getOrderDetailsByDate(date);
            // Return HTTP 200 if orders are found
            return ResponseEntity.ok(orderDetails);
        } catch (InvalidOrderException e) {
            // Handle custom exception for no orders
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UserNotFoundException e) {
            // Handle custom exception for no orders
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NoOrderDetailsFoundException e) {
            // Handle custom exception for no orders
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            // Handle any unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
        
        //2025-05-21
    }


    @GetMapping("admin/dateRange")
    public ResponseEntity<?> getOrderDetailsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // Call service method to fetch orders within the date range
            List<OrderDetailsDTO> orderDetails = orderDetailsService.getOrderDetailsByDateRange(startDate, endDate);

            // Return HTTP 200 with the fetched data
            return ResponseEntity.ok(orderDetails);

        } catch (IllegalArgumentException e) {
            // Handle invalid date ranges
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (NoOrderDetailsFoundException e) {
            // Handle case where no orders are found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDetailsDTO orderDetailsDTO) {
        try {
            // Attempt to create the order
            OrderDetailsDTO savedOrder = orderDetailsService.createOrder(orderDetailsDTO);
            // Return the created order with HTTP status 201 (Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);

        } catch (UserNotFoundException ex) {
            // Handle case where the user is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()+"inside");

        } catch (BooksNotFoundException ex) {
            // Handle case where a book in the order is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

        } catch (InvalidOrderException ex) {
            // Handle case where the order details are invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

        } catch (Exception ex) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }
    }
    
    @PostMapping("/admin/changeStatus/{id}/{status}")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @PathVariable String status) {
        try {
            // Call service method to change the status
            String result = orderDetailsService.changeOrderStatus(id, status);

            // Return HTTP 200 with the result
            return ResponseEntity.ok(result);

        } catch (OrderNotFoundException e) {
            // Handle case where the order is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            // Handle invalid status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    
}
