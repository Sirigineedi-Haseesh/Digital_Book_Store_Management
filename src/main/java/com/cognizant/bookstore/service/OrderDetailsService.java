package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.OrderBookAssociationDTO;
import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.exceptions.BookNotFoundException;
import com.cognizant.bookstore.exceptions.InvalidOrderException;
import com.cognizant.bookstore.exceptions.OrderNotFoundException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
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
     * Retrieve order details by order ID.
     *
     * @param orderId order ID
     * @return order details in DTO format
     */
    public OrderDetailsDTO getOrderDetailsByOrderId(Long orderId) {
        OrderDetails orderDetails = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for ID: " + orderId));
        return modelMapper.map(orderDetails, OrderDetailsDTO.class);
    }

    /**
     * Create a new order.
     *
     * @param orderDetailsDTO order details in DTO format
     * @return saved order details in DTO format
     */
    @Transactional
    public OrderDetailsDTO createOrder(OrderDetailsDTO orderDetailsDTO) {
        User user = userRepository.findById(orderDetailsDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (orderDetailsDTO.getOrderBooks() == null || orderDetailsDTO.getOrderBooks().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one book");
        }

        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setUser(user);
        orderDetails.setOrderDate(LocalDateTime.now());

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
}
