package com.cognizant.bookstore.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.dto.BookDTO;
import com.cognizant.bookstore.dto.OrderBookAssociationDTO;
import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.model.OrderBookAssociation;
import com.cognizant.bookstore.model.OrderDetails;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;
import com.cognizant.bookstore.repository.OrderRepository;
import com.cognizant.bookstore.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class OrderDetailsService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private InventoryRepository inventoryRepository;
	
	
	public List<OrderDetailsDTO> getOrderDetails() {
		List<OrderDetails> orderDetails = orderRepository.findAll();
		return orderDetails.stream()
                .map(order -> modelMapper.map(order, OrderDetailsDTO.class))
                .collect(Collectors.toList());
	}
	
	
	@Transactional
	public OrderDetailsDTO createOrder(OrderDetailsDTO orderDetailsDTO) {
	    // 1️⃣ Fetch user from database using userId
	    User user = userRepository.findById(orderDetailsDTO.getUserId())
	                  .orElseThrow(() -> new RuntimeException("User not found"));
	    if (orderDetailsDTO.getOrderBooks() == null || orderDetailsDTO.getOrderBooks().isEmpty()) {
	        throw new RuntimeException("Order must contain at least one book");
	    }
	    // 2️⃣ Initialize order entity
	    OrderDetails orderDetails = new OrderDetails();
	    orderDetails.setUser(user);
	    orderDetails.setOrderDate(LocalDateTime.now()); // Auto-set order date

	    //  Initialize total amount
	    double totalAmount = 0.0;

	    //  Process each book in the order & create associations
	    Set<OrderBookAssociation> orderBooks = new HashSet<>();
	    for (OrderBookAssociationDTO orderBookDTO : orderDetailsDTO.getOrderBooks()) {
	        Book book = bookRepository.findById(orderBookDTO.getBookId())
	                     .orElseThrow(() -> new RuntimeException("Book not found"));

	        OrderBookAssociation orderBook = new OrderBookAssociation();
	        orderBook.setOrder(orderDetails);
	        orderBook.setBook(book);
	        orderBook.setQuantity(orderBookDTO.getQuantity());

	        orderBooks.add(orderBook);
	        
	        
//	        updating the inventory when user orders respective book
	        Inventory inventory = inventoryRepository.findByBookBookId(book.getBookId());
			int updatedStock = inventory.getStock()-orderBookDTO.getQuantity();
			inventory.setStock(updatedStock);
			inventoryRepository.save(inventory);
			
			
	        // Calculate total price (quantity * price of book)
	        totalAmount += book.getPrice() * orderBookDTO.getQuantity();
	    }

	    //  Save order & associations in the database
	    orderDetails.setTotalAmount(totalAmount);
	    
	    orderDetails.setOrderBooks(orderBooks);
	    OrderDetails savedOrder = orderRepository.save(orderDetails);

	    // Return saved order details as DTO
	    return modelMapper.map(savedOrder, OrderDetailsDTO.class);
	}
}
