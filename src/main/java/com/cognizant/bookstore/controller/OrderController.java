package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.exceptions.OrderNotFoundException;
import com.cognizant.bookstore.service.OrderDetailsService;

import jakarta.validation.Valid;

@RestController
public class OrderController {
	
    @Autowired
    private OrderDetailsService orderDetailsService;
    
    @GetMapping("/getOrderDetails")
    public ResponseEntity<List<OrderDetailsDTO>> fetchOrderDetails() {
        List<OrderDetailsDTO> orderDetails = orderDetailsService.getOrderDetails();
        if (orderDetails.isEmpty()) {
            return ResponseEntity.noContent().build(); // httpStatus 204 content
        }
        return ResponseEntity.ok(orderDetails); // httpStatus 200
    }
    
    @GetMapping("/getOrderDetails/{id}")
    public ResponseEntity<OrderDetailsDTO> fetchOrderDetailsById(@PathVariable Long id) {
        try {
            OrderDetailsDTO orderDetails = orderDetailsService.getOrderDetailsById(id);
            return ResponseEntity.ok(orderDetails); // httpStatus 200
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // httpStatus 404
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<OrderDetailsDTO> createOrder(@Valid @RequestBody OrderDetailsDTO orderDetailsDTO) {
        OrderDetailsDTO savedOrder = orderDetailsService.createOrder(orderDetailsDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);  // httpStatus 201
    }
  
}
