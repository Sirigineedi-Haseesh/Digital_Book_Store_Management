package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.OrderBookAssociationDTO;
import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.exceptions.ResourceNotFoundException;


import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.OrderBookAssociation;
import com.cognizant.bookstore.model.OrderDetails;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.OrderRepository;
import com.cognizant.bookstore.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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

    /**
     * Retrieve all orders.
     *
     * @return list of all orders in DTO format
     */
    public List<OrderDetailsDTO> getOrderDetails() {
        List<OrderDetails> orderDetails = orderRepository.findAll();
        return orderDetails.stream()
                .map(order -> modelMapper.map(order, OrderDetailsDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Create a new order.
     *
     * @param orderDetailsDTO order details in DTO format
     * @return saved order details in DTO format
     */
    @Transactional
    public OrderDetailsDTO createOrder(OrderDetailsDTO orderDetailsDTO) {
        // Validate user
        User user = userRepository.findById(orderDetailsDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + orderDetailsDTO.getUserId()));

        // Validate order books
        if (orderDetailsDTO.getOrderBooks() == null || orderDetailsDTO.getOrderBooks().isEmpty()) {
            throw new RuntimeException("Order must contain at least one book");
        }

        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setUser(user);
        orderDetails.setOrderDate(LocalDateTime.now());

        double totalAmount = 0.0;
        Set<OrderBookAssociation> orderBooks = new HashSet<>();

        for (OrderBookAssociationDTO orderBookDTO : orderDetailsDTO.getOrderBooks()) {
            // Fetch and validate the book
            Book book = bookRepository.findById(orderBookDTO.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found for ID: " + orderBookDTO.getBookId()));

            // Validate quantity
            if (orderBookDTO.getQuantity() <= 0) {
                throw new RuntimeException("Invalid quantity provided for book ID: " + book.getBookId());
            }

            // Call InventoryService to reduce stock
            inventoryService.reduceStockOnOrder(book.getBookId(), orderBookDTO.getQuantity());

            // Create the OrderBookAssociation
            OrderBookAssociation orderBook = new OrderBookAssociation();
            orderBook.setOrder(orderDetails);
            orderBook.setBook(book);
            orderBook.setQuantity(orderBookDTO.getQuantity());

            orderBooks.add(orderBook);

            // Calculate total amount
            totalAmount += book.getPrice() * orderBookDTO.getQuantity();
        }

        // Set order details
        orderDetails.setTotalAmount(totalAmount);
        orderDetails.setOrderBooks(orderBooks);

        // Save and return order details
        OrderDetails savedOrder = orderRepository.save(orderDetails);
        return modelMapper.map(savedOrder, OrderDetailsDTO.class);
    }
}
