package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.service.OrderDetailsService;

@RestController
public class OrderController {
	
	@Autowired
	private OrderDetailsService orderDetailsService;
	
	@GetMapping("/getOrderDetails")
	public ResponseEntity<List<OrderDetailsDTO>> fetchOrderDetails(){
		List<OrderDetailsDTO> orderDetails = orderDetailsService.getOrderDetails();
		if (orderDetails.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
		return ResponseEntity.ok(orderDetails);
	}
	
	@PostMapping("/create")
    public ResponseEntity<OrderDetailsDTO> createOrder(@RequestBody OrderDetailsDTO orderDetailsDTO) {
        OrderDetailsDTO savedOrder = orderDetailsService.createOrder(orderDetailsDTO);
        return ResponseEntity.status(201).body(savedOrder);
    }
}
