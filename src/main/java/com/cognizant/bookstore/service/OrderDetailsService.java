package com.cognizant.bookstore.service;
 
import com.cognizant.bookstore.dto.OrderBookAssociationDTO;
import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.exceptions.BookNotFoundException;
import com.cognizant.bookstore.exceptions.InvalidOrderException;
import com.cognizant.bookstore.exceptions.NoOrderDetailsFoundException;
import com.cognizant.bookstore.exceptions.OrderNotFoundException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.OrderBookAssociation;
import com.cognizant.bookstore.model.OrderDetails;
import com.cognizant.bookstore.model.OrderStatus;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.OrderRepository;
import com.cognizant.bookstore.repository.UserRepository;
 
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
 
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
    private InventoryService inventoryService;
 
    
    public List<OrderDetailsDTO> getOrderDetailsByDate(LocalDate date) {
        List<OrderDetails> orderDetails = orderRepository.findByOrderDate(date);

        if (orderDetails.isEmpty()) {
            throw new NoOrderDetailsFoundException("No orders found for the date: " + date);
        }

        return orderDetails.stream()
                .map(order -> modelMapper.map(order, OrderDetailsDTO.class))
                .collect(Collectors.toList());
    }

    public List<OrderDetailsDTO> getOrderDetailsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<OrderDetails> orderDetails = orderRepository.findByOrderDateBetween(startDate, endDate);

        if (orderDetails.isEmpty()) {
            throw new NoOrderDetailsFoundException("No orders found between the dates: " + startDate + " and " + endDate);
        }

        return orderDetails.stream()
                .map(order -> modelMapper.map(order, OrderDetailsDTO.class))
                .collect(Collectors.toList());
    }
    
    
    @Transactional
    public OrderDetailsDTO createOrder(OrderDetailsDTO orderDetailsDTO) {
        User user = userRepository.findById(orderDetailsDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
 
        if (orderDetailsDTO.getOrderBooks() == null || orderDetailsDTO.getOrderBooks().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one book");
        }
 
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setUser(user);
        orderDetails.setOrderDate(LocalDate.now());
 
        double totalAmount = 0.0;
        Set<OrderBookAssociation> orderBooks = new HashSet<>();
 
        for (OrderBookAssociationDTO orderBookDTO : orderDetailsDTO.getOrderBooks()) {
            // Fetch the book by ID
            Book book = bookRepository.findById(orderBookDTO.getBookId())
                    .orElseThrow(() -> new BookNotFoundException("Book not found"));
 
            // Validate book ID and quantity
            if (orderBookDTO == null || orderBookDTO.getQuantity() <= 0) {
                throw new InvalidOrderException("Invalid quantity provided.");
            }
 
            // Call InventoryService to reduce stock
            inventoryService.reduceStockOnOrder(book.getTitle(), orderBookDTO.getQuantity());
 
            OrderBookAssociation orderBook = new OrderBookAssociation();
            orderBook.setOrder(orderDetails);
            orderBook.setBook(book);
            orderBook.setQuantity(orderBookDTO.getQuantity());
 
            orderBooks.add(orderBook);
 
            totalAmount += book.getPrice() * orderBookDTO.getQuantity();
        }
 
        orderDetails.setTotalAmount(totalAmount);
        orderDetails.setOrderBooks(orderBooks);
 
        OrderDetails savedOrder = orderRepository.save(orderDetails);
 
        return modelMapper.map(savedOrder, OrderDetailsDTO.class);
    }

    public String changeOrderStatus(Long id, String status) {
        // Find the order by ID
        Optional<OrderDetails> orderOptional = orderRepository.findById(id);

        // If the order does not exist, throw a custom exception
        if (orderOptional.isEmpty()) {
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }

        // Validate the status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        // Update the status
        OrderDetails order = orderOptional.get();
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderRepository.save(order);

        // Return a confirmation message
        return "Order status updated successfully to: " + status;
    }
    
    private boolean isValidStatus(String status) {
        try {
            OrderStatus.valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}