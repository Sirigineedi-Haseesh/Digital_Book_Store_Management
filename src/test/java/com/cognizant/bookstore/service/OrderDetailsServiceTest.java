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
import com.cognizant.bookstore.exceptions.NoOrderDetailsFoundException;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.OrderDetails;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.OrderRepository;
import com.cognizant.bookstore.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
        User mockUser = new User();
        mockUser.setUserId(1L);

        Book mockBook = new Book();
        mockBook.setBookId(1L);
        mockBook.setTitle("TestBook");
        mockBook.setPrice(100L);

        OrderBookAssociationDTO orderBookDTO = new OrderBookAssociationDTO();
        orderBookDTO.setBookId(1L);
        orderBookDTO.setQuantity(2);

        OrderDetailsDTO inputOrderDetailsDTO = new OrderDetailsDTO();
        inputOrderDetailsDTO.setUserId(1L);
        inputOrderDetailsDTO.setOrderBooks(Set.of(orderBookDTO));

        OrderDetails mockSavedOrder = new OrderDetails();
        mockSavedOrder.setOrderId(1L);
        mockSavedOrder.setUser(mockUser);
        mockSavedOrder.setOrderDate(LocalDate.now());
        mockSavedOrder.setTotalAmount(200.0);

        OrderDetailsDTO outputOrderDetailsDTO = new OrderDetailsDTO();
        outputOrderDetailsDTO.setOrderId(1L);
        outputOrderDetailsDTO.setUserId(1L);
        outputOrderDetailsDTO.setTotalAmount(200.0);

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
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderDetailsService.createOrder(new OrderDetailsDTO()));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testCreateOrder_bookNotFound() {
        User mockUser = new User();
        mockUser.setUserId(1L);

        OrderBookAssociationDTO orderBookDTO = new OrderBookAssociationDTO();
        orderBookDTO.setBookId(1L);
        orderBookDTO.setQuantity(2);

        OrderDetailsDTO inputOrderDetailsDTO = new OrderDetailsDTO();
        inputOrderDetailsDTO.setUserId(1L);
        inputOrderDetailsDTO.setOrderBooks(Set.of(orderBookDTO));

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderDetailsService.createOrder(inputOrderDetailsDTO));

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void testCreateOrder_invalidQuantity() {
        User mockUser = new User();
        mockUser.setUserId(1L);

        Book mockBook = new Book();
        mockBook.setBookId(1L);

        OrderBookAssociationDTO orderBookDTO = new OrderBookAssociationDTO();
        orderBookDTO.setBookId(1L);
        orderBookDTO.setQuantity(0);

        OrderDetailsDTO mockOrderDTO = new OrderDetailsDTO();
        mockOrderDTO.setUserId(1L);
        mockOrderDTO.setOrderBooks(Set.of(orderBookDTO));

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderDetailsService.createOrder(mockOrderDTO));

        assertEquals("Invalid quantity provided.", exception.getMessage());
    }

    @Test
    void testGetOrderDetailsByDate_Success() {
        LocalDate date = LocalDate.now();

        OrderDetails mockOrder = new OrderDetails();
        mockOrder.setOrderDate(date);
        mockOrder.setTotalAmount(500.0);

        OrderDetailsDTO mockOrderDTO = new OrderDetailsDTO();
        mockOrderDTO.setOrderDate(date);
        mockOrderDTO.setTotalAmount(500.0);

        when(orderRepository.findByOrderDate(date)).thenReturn(List.of(mockOrder));
        when(modelMapper.map(mockOrder, OrderDetailsDTO.class)).thenReturn(mockOrderDTO);

        List<OrderDetailsDTO> result = orderDetailsService.getOrderDetailsByDate(date);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result size should match");
        assertEquals(500.0, result.get(0).getTotalAmount(), "Total amount should match");
        verify(orderRepository, times(1)).findByOrderDate(date);
    }

    @Test
    void testGetOrderDetailsByDate_NoOrdersFound() {
        LocalDate date = LocalDate.now();
        when(orderRepository.findByOrderDate(date)).thenReturn(Collections.emptyList());

        NoOrderDetailsFoundException exception = assertThrows(NoOrderDetailsFoundException.class,
            () -> orderDetailsService.getOrderDetailsByDate(date));

        assertEquals("No orders found for the date: " + date, exception.getMessage());
        verify(orderRepository, times(1)).findByOrderDate(date);
    }

    @Test
    void testGetOrderDetailsByDateRange_Success() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        OrderDetails mockOrder = new OrderDetails();
        mockOrder.setOrderDate(endDate);
        mockOrder.setTotalAmount(800.0);

        OrderDetailsDTO mockOrderDTO = new OrderDetailsDTO();
        mockOrderDTO.setOrderDate(endDate);
        mockOrderDTO.setTotalAmount(800.0);

        when(orderRepository.findByOrderDateBetween(startDate, endDate)).thenReturn(List.of(mockOrder));
        when(modelMapper.map(mockOrder, OrderDetailsDTO.class)).thenReturn(mockOrderDTO);

        List<OrderDetailsDTO> result = orderDetailsService.getOrderDetailsByDateRange(startDate, endDate);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result size should match");
        assertEquals(800.0, result.get(0).getTotalAmount(), "Total amount should match");
        verify(orderRepository, times(1)).findByOrderDateBetween(startDate, endDate);
    }

    @Test
    void testGetOrderDetailsByDateRange_NoOrdersFound() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(orderRepository.findByOrderDateBetween(startDate, endDate)).thenReturn(Collections.emptyList());

        NoOrderDetailsFoundException exception = assertThrows(NoOrderDetailsFoundException.class,
            () -> orderDetailsService.getOrderDetailsByDateRange(startDate, endDate));

        assertEquals("No orders found between the dates: " + startDate + " and " + endDate, exception.getMessage());
        verify(orderRepository, times(1)).findByOrderDateBetween(startDate, endDate);
    }
}
