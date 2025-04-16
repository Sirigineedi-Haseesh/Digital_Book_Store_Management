package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cognizant.bookstore.dto.OrderBookAssociationDTO;
import com.cognizant.bookstore.dto.OrderDetailsDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.OrderDetails;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.OrderRepository;
import com.cognizant.bookstore.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class OrderDetailsServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderDetailsService orderDetailsService;

    private OrderDetails orderDetails;
    private OrderDetailsDTO orderDetailsDTO;
    private User user;
    private Book book;
    private OrderBookAssociationDTO orderBookDTO;

    @BeforeEach
    public void setUp() {
        // Mock User
        user = new User();
        user.setUserId(1L);
        user.setUserName("Test User");

        // Mock Book
        book = new Book();
        book.setBookId(1L);
        book.setAuthorName("Test Book");
        book.setPrice(100);

        // Mock OrderBookAssociationDTO
        orderBookDTO = new OrderBookAssociationDTO();
        orderBookDTO.setBookId(1L);
        orderBookDTO.setQuantity(2);

        // Mock OrderDetailsDTO
        orderDetailsDTO = new OrderDetailsDTO();
        orderDetailsDTO.setUserId(1L);
        orderDetailsDTO.setOrderBooks(new HashSet<>(Collections.singletonList(orderBookDTO)));

        // Mock OrderDetails
        orderDetails = new OrderDetails();
        orderDetails.setOrderId(1L);
        orderDetails.setUser(user);
        orderDetails.setOrderDate(LocalDateTime.now());
        orderDetails.setTotalAmount(200.0);
        orderDetails.setOrderBooks(new HashSet<>());
    }

    @Test
    public void testGetOrderDetails() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(orderDetails));
        when(modelMapper.map(orderDetails, OrderDetailsDTO.class)).thenReturn(orderDetailsDTO);

        List<OrderDetailsDTO> result = orderDetailsService.getOrderDetails();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderDetailsDTO, result.get(0));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testCreateOrder() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(inventoryService).reduceStockOnOrder(1L, 2); // Mock inventory reduction

        when(orderRepository.save(any(OrderDetails.class))).thenReturn(orderDetails);
        when(modelMapper.map(orderDetails, OrderDetailsDTO.class)).thenReturn(orderDetailsDTO);

        OrderDetailsDTO result = orderDetailsService.createOrder(orderDetailsDTO);

        assertNotNull(result);
        assertEquals(orderDetailsDTO, result);
        verify(userRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).findById(1L);
        verify(inventoryService, times(1)).reduceStockOnOrder(1L, 2);
        verify(orderRepository, times(1)).save(any(OrderDetails.class));
    }

    @Test
    public void testCreateOrderUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderDetailsService.createOrder(orderDetailsDTO);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoInteractions(bookRepository);
        verifyNoInteractions(inventoryService);
        verifyNoInteractions(orderRepository);
    }

    @Test
    public void testCreateOrderBookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderDetailsService.createOrder(orderDetailsDTO);
        });

        assertEquals("Book not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).findById(1L);
        verifyNoInteractions(inventoryService);
        verifyNoInteractions(orderRepository);
    }
//
//    @Test
//    public void testCreateOrderInvalidQuantity() {
//        orderBookDTO.setQuantity(0); // Invalid quantity
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            orderDetailsService.createOrder(orderDetailsDTO);
//        });
//
//        assertEquals("Invalid quantity provided.", exception.getMessage());
//        verifyNoInteractions(userRepository);
//        verifyNoInteractions(bookRepository);
//        verifyNoInteractions(inventoryService);
//        verifyNoInteractions(orderRepository);
//    }

//    @Test
//    public void testCreateOrderEmptyOrderBooks() {
//        orderDetailsDTO.setOrderBooks(new HashSet<>());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            orderDetailsService.createOrder(orderDetailsDTO);
//        });
//
//        assertEquals("Order must contain at least one book", exception.getMessage());
//        verifyNoInteractions(userRepository);
//        verifyNoInteractions(bookRepository);
//        verifyNoInteractions(inventoryService);
//        verifyNoInteractions(orderRepository);
//    }
}
