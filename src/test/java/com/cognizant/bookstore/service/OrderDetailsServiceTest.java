package com.cognizant.bookstore.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import com.cognizant.bookstore.dto.OrderBookAssociationDTO;
import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.OrderDetails;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.OrderRepository;
import com.cognizant.bookstore.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

class OrderDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderDetailsService orderDetailsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_success() {
        // Arrange
        // Mock User
        User mockUser = new User();
        mockUser.setUserId(1L);

        // Mock Book
        Book mockBook = new Book();
        mockBook.setBookId(1L);
        mockBook.setTitle("TestBook");
        mockBook.setPrice(100L);

        // Mock OrderBookAssociationDTO
        OrderBookAssociationDTO orderBookDTO = new OrderBookAssociationDTO();
        orderBookDTO.setBookId(1L);
        orderBookDTO.setQuantity(2);

        // Mock Input OrderDetailsDTO
        OrderDetailsDTO inputOrderDetailsDTO = new OrderDetailsDTO();
        inputOrderDetailsDTO.setUserId(1L);
        inputOrderDetailsDTO.setOrderBooks(Set.of(orderBookDTO));

        // Mock Saved OrderDetails
        OrderDetails mockSavedOrder = new OrderDetails();
        mockSavedOrder.setOrderId(1L);
        mockSavedOrder.setUser(mockUser);
        mockSavedOrder.setOrderDate(LocalDateTime.now());
        mockSavedOrder.setTotalAmount(200.0);

        OrderDetailsDTO outputOrderDetailsDTO = new OrderDetailsDTO();
        outputOrderDetailsDTO.setOrderId(1L);
        outputOrderDetailsDTO.setUserId(1L);
        outputOrderDetailsDTO.setTotalAmount(200.0);

        // Mocking Repository and Service Methods
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));
        when(orderRepository.save(any(OrderDetails.class))).thenReturn(mockSavedOrder);
        when(modelMapper.map(mockSavedOrder, OrderDetailsDTO.class)).thenReturn(outputOrderDetailsDTO);

        // Act
        OrderDetailsDTO result = orderDetailsService.createOrder(inputOrderDetailsDTO);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getUserId(), "User ID should match");
        assertEquals(200.0, result.getTotalAmount(), "Total amount should match");
        assertEquals(1L, result.getOrderId(), "Order ID should match");
    }

    @Test
    void testCreateOrder_userNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderDetailsService.createOrder(new OrderDetailsDTO()));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testCreateOrder_bookNotFound() {
        // Arrange
        User mockUser = new User();
        mockUser.setUserId(1L);

        OrderBookAssociationDTO orderBookDTO = new OrderBookAssociationDTO();
        orderBookDTO.setBookId(1L); // Book ID that does not exist
        orderBookDTO.setQuantity(2);

        OrderDetailsDTO inputOrderDetailsDTO = new OrderDetailsDTO();
        inputOrderDetailsDTO.setUserId(1L); // Valid user ID
        inputOrderDetailsDTO.setOrderBooks(Set.of(orderBookDTO));

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty()); // Simulating book not found

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderDetailsService.createOrder(inputOrderDetailsDTO));

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void testCreateOrder_invalidQuantity() {
        // Arrange
        User mockUser = new User();
        mockUser.setUserId(1L);

        Book mockBook = new Book();
        mockBook.setBookId(1L);

        OrderBookAssociationDTO orderBookDTO = new OrderBookAssociationDTO();
        orderBookDTO.setBookId(1L);
        orderBookDTO.setQuantity(0); // Invalid quantity

        OrderDetailsDTO mockOrderDTO = new OrderDetailsDTO();
        mockOrderDTO.setUserId(1L);
        mockOrderDTO.setOrderBooks(Set.of(orderBookDTO)); // Populate order books

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderDetailsService.createOrder(mockOrderDTO));

        assertEquals("Invalid quantity provided.", exception.getMessage());
    }
}