package com.cognizant.bookstore.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.dto.OrderBookAssociationDTO;
import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.exceptions.OrderNotFoundException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.exceptions.BookNotFoundException;
import com.cognizant.bookstore.exceptions.InvalidOrderException;
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
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
        log.info("Fetching all order details");
        List<OrderDetails> orderDetails = orderRepository.findAll();
        log.info("Total orders found: {}", orderDetails.size());
        return orderDetails.stream().map(order -> modelMapper.map(order, OrderDetailsDTO.class))
                .collect(Collectors.toList());
    }

    public OrderDetailsDTO getOrderDetailsById(Long orderId) {
        log.info("Fetching order details for ID: {}", orderId);
        OrderDetails orderDetails = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        return modelMapper.map(orderDetails, OrderDetailsDTO.class);
    }

    @Transactional
    public OrderDetailsDTO createOrder(OrderDetailsDTO orderDetailsDTO) {
        log.info("Creating new order for user ID: {}", orderDetailsDTO.getUserId());

        // Fetch user by ID
        User user = userRepository.findById(orderDetailsDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + orderDetailsDTO.getUserId()));

        // Validate order books
        if (orderDetailsDTO.getOrderBooks() == null || orderDetailsDTO.getOrderBooks().isEmpty()) {
            log.error("Order must contain at least one book");
            throw new InvalidOrderException("Order must contain at least one book");
        }

        // Create new order details
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setUser(user);
        orderDetails.setOrderDate(LocalDateTime.now());

        double totalAmount = 0.0;
        Set<OrderBookAssociation> orderBooks = new HashSet<>();

        // Process each order book
        for (OrderBookAssociationDTO orderBookDTO : orderDetailsDTO.getOrderBooks()) {
            Book book = bookRepository.findById(orderBookDTO.getBookId())
                    .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + orderBookDTO.getBookId()));
            log.info("Book found: {}", book.getTitle());

            OrderBookAssociation orderBook = new OrderBookAssociation();
            orderBook.setOrder(orderDetails);
            orderBook.setBook(book);
            orderBook.setQuantity(orderBookDTO.getQuantity());

            orderBooks.add(orderBook);

            // Update inventory
            Inventory inventory = inventoryRepository.findByBookBookId(book.getBookId());
            int updatedStock = inventory.getStock() - orderBookDTO.getQuantity();
            inventory.setStock(updatedStock);
            inventoryRepository.save(inventory);
            log.info("Inventory updated for book ID: {}. New stock: {}", book.getBookId(), updatedStock);

            // Calculate total amount
            totalAmount += book.getPrice() * orderBookDTO.getQuantity();
        }

        // Set total amount and o54rder books
        orderDetails.setTotalAmount(totalAmount);
        orderDetails.setOrderBooks(orderBooks);

        // Save order details
        OrderDetails savedOrder = orderRepository.save(orderDetails);
        log.info("Order created with ID: {}", savedOrder.getOrderId());

        return modelMapper.map(savedOrder, OrderDetailsDTO.class);
    }
}
