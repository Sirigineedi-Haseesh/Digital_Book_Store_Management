package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.exceptions.*;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InventoryServiceImp inventoryService;

    private Inventory inventory;
    private Book book;
    private InventoryDTO inventoryDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up reusable objects
        inventory = new Inventory();
        inventory.setStock(100);

        book = new Book();
        book.setTitle("Sample Book");
        book.setInventory(inventory);

        inventoryDTO = new InventoryDTO();
        inventoryDTO.setStock(100);
    }

    // Test for getInventoryByName
    @Test
    void testGetInventoryByName_Success() {
        when(bookRepository.findByBookName("Sample Book")).thenReturn(book);
        when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

        InventoryDTO result = inventoryService.getInventoryByName("Sample Book");

        assertNotNull(result);
        assertEquals(100, result.getStock());
        verify(bookRepository, times(1)).findByBookName("Sample Book");
    }

    @Test
    void testGetInventoryByName_BookNotFound() {
        when(bookRepository.findByBookName("Nonexistent Book")).thenReturn(null);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, 
            () -> inventoryService.getInventoryByName("Nonexistent Book"));

        assertEquals("Book not found or inventory not available for the title: Nonexistent Book", exception.getMessage());
        verify(bookRepository, times(1)).findByBookName("Nonexistent Book");
    }

    @Test
    void testGetInventoryByName_InventoryNotFound() {
        book.setInventory(null); // No inventory associated with the book
        when(bookRepository.findByBookName("Sample Book")).thenReturn(book);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, 
            () -> inventoryService.getInventoryByName("Sample Book"));

        assertEquals("Book not found or inventory not available for the title: Sample Book", exception.getMessage());
    }

    // Test for getAllInventories
    @Test
    void testGetAllInventories_Success() {
        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));
        when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

        List<InventoryDTO> result = inventoryService.getAllInventories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getStock());
    }

    @Test
    void testGetAllInventories_NoInventoriesFound() {
        when(inventoryRepository.findAll()).thenReturn(Collections.emptyList());

        NoInventoriesFoundException exception = assertThrows(NoInventoriesFoundException.class, 
            () -> inventoryService.getAllInventories());

        assertEquals("No Inventories are Present", exception.getMessage());
    }

    // Test for reduceStockOnOrder
    @Test
    void testReduceStockOnOrder_Success() {
        when(bookRepository.findByBookName("Sample Book")).thenReturn(book);
        when(inventoryRepository.findByBookBookId(book.getBookId())).thenReturn(inventory);

        inventoryService.reduceStockOnOrder("Sample Book", 10);

        assertEquals(90, inventory.getStock());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void testReduceStockOnOrder_BookNotFound() {
        when(bookRepository.findByBookName("Nonexistent Book")).thenReturn(null);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, 
            () -> inventoryService.reduceStockOnOrder("Nonexistent Book", 10));

        assertEquals("Book not found or inventory not available for the title: Nonexistent Book", exception.getMessage());
        verify(bookRepository, times(1)).findByBookName("Nonexistent Book");
    }

    @Test
    void testReduceStockOnOrder_InventoryNotFound() {
        when(bookRepository.findByBookName("Sample Book")).thenReturn(book);
        when(inventoryRepository.findByBookBookId(book.getBookId())).thenReturn(null);

        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class, 
            () -> inventoryService.reduceStockOnOrder("Sample Book", 10));

        assertEquals("Inventory not found for the book: Sample Book", exception.getMessage());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void testReduceStockOnOrder_InsufficientStock() {
        inventory.setStock(5); // Set inventory stock below ordered quantity
        when(bookRepository.findByBookName("Sample Book")).thenReturn(book);
        when(inventoryRepository.findByBookBookId(book.getBookId())).thenReturn(inventory);

        InsufficientStockException exception = assertThrows(InsufficientStockException.class, 
            () -> inventoryService.reduceStockOnOrder("Sample Book", 10));

        assertEquals("Insufficient stock available for the book: Sample Book", exception.getMessage());
        verify(inventoryRepository, never()).save(any());
    }
}
