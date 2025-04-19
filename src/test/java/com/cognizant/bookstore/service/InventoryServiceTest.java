package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.exceptions.BookNotFoundException;
import com.cognizant.bookstore.exceptions.InsufficientStockException;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.InventoryRepository;
import com.cognizant.bookstore.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InventoryServiceImp inventoryService;

    private Inventory inventory;
    private InventoryDTO inventoryDTO;
    private Book book;

    @BeforeEach
    public void setUp() {
        // Mock Inventory
        inventory = new Inventory();
        inventory.setInventoryId(1L);
        inventory.setStock(150);

        // Mock InventoryDTO
        inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(1L);
        inventoryDTO.setStock(150);

        // Mock Book
        book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setInventory(inventory);

        inventory.setBook(book);
    }

    @Test
    public void testGetInventoryByName() {
        when(bookRepository.findByBookName("Test Book")).thenReturn(Optional.of(book).get());
        when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

        InventoryDTO result = inventoryService.getInventoryByName("Test Book");

        assertNotNull(result);
        assertEquals(inventoryDTO, result);
        verify(bookRepository, times(1)).findByBookName("Test Book");
    }

    @Test
    public void testGetInventoryByNameNotFound() {
        when(bookRepository.findByBookName("Unknown Book")).thenReturn(null);

        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            inventoryService.getInventoryByName("Unknown Book");
        });

        assertEquals("Book not found or inventory not available for the title: Unknown Book", exception.getMessage());
        verify(bookRepository, times(1)).findByBookName("Unknown Book");
    }

    @Test
    public void testGetAllInventories() {
        List<Inventory> inventories = Arrays.asList(inventory);
        when(inventoryRepository.findAll()).thenReturn(inventories);
        when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

        List<InventoryDTO> result = inventoryService.getAllInventories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(inventoryDTO, result.get(0));
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    public void testReduceStockOnOrder() {
        // Mock Repository Behavior
        when(bookRepository.findByBookName("TestBook")).thenReturn(Optional.of(book).get());
        when(inventoryRepository.findByBookBookId(1L)).thenReturn(inventory);

        // Call the service method
        inventoryService.reduceStockOnOrder("TestBook", 50);

        // Assertions
        assertEquals(100, inventory.getStock()); // Ensure stock is reduced correctly
        verify(inventoryRepository, times(1)).save(inventory); // Verify inventory save operation
        verify(bookRepository, times(1)).findByBookName("TestBook"); // Verify book lookup
        verify(inventoryRepository, times(1)).findByBookBookId(1L); // Verify inventory lookup
    }

    @Test
    public void testReduceStockOnOrderInsufficientStock() {
        // Mocking Inventory with insufficient stock
        inventory.setStock(50); // Stock is less than the requested quantity

        // Mock Repository Behavior
        when(bookRepository.findByBookName("Test Book")).thenReturn(Optional.of(book).get());
        when(inventoryRepository.findByBookBookId(1L)).thenReturn(inventory);

        // Assert that an InsufficientStockException is thrown
        Exception exception = assertThrows(InsufficientStockException.class, () -> {
            inventoryService.reduceStockOnOrder("Test Book", 200); // Requested quantity exceeds available stock
        });

        // Verify exception message
        assertEquals("Insufficient stock available for the book: Test Book", exception.getMessage());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    public void testReduceStockOnOrderInventoryNotFound() {
        // Mock Repository Behavior
        when(bookRepository.findByBookName("Test Book")).thenReturn(Optional.of(book).get());
        when(inventoryRepository.findByBookBookId(1L)).thenReturn(null); // Inventory not found for the book

        // Assert that a RuntimeException is thrown
        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reduceStockOnOrder("Test Book", 50); // Attempt to reduce stock for nonexistent inventory
        });

        // Verify exception message
        assertEquals("Inventory not found for the book: Test Book", exception.getMessage());

        // Verify no save operation occurred
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

}
