package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.cognizant.bookstore.dto.InventoryDTO;
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
        inventory = new Inventory();
        inventory.setInventoryId(1L);
        inventory.setStock(150);

        inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(1L);
        inventoryDTO.setStock(150);

        book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setInventory(inventory);

        inventory.setBook(book);
    }

    @Test
    public void testGetInventoryByName() {
        when(bookRepository.findByBookName("Test Book")).thenReturn(book);
        when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

        InventoryDTO result = inventoryService.getInventoryByName("Test Book");

        assertNotNull(result);
        assertEquals(inventoryDTO, result);
    }

    @Test
    public void testGetInventoryByNameNotFound() {
        when(bookRepository.findByBookName("Unknown Book")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.getInventoryByName("Unknown Book");
        });

        assertEquals("Inventory not found for the specified book title: Unknown Book", exception.getMessage());
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
    }

//    @Test
//    public void testDeleteInventory() {
//        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
//        when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);
//
//        InventoryDTO result = inventoryService.deleteInventory(1L);
//
//        assertNotNull(result);
//        verify(inventoryRepository, times(1)).delete(inventory);
//    }
//
//    @Test
//    public void testDeleteInventoryNotFound() {
//        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            inventoryService.deleteInventory(1L);
//        });
//
//        assertEquals("Inventory not found for the specified ID: 1", exception.getMessage());
//    }

    @Test
    public void testReduceStockOnOrder() {
        when(inventoryRepository.findByBookBookId(1L)).thenReturn(inventory);

        inventoryService.reduceStockOnOrder(1L, 50);

        assertEquals(100, inventory.getStock());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    public void testReduceStockOnOrderInsufficientStock() {
        when(inventoryRepository.findByBookBookId(1L)).thenReturn(inventory);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reduceStockOnOrder(1L, 200);
        });

        assertEquals("Insufficient stock available for the book ID: 1", exception.getMessage());
    }

    @Test
    public void testReduceStockOnOrderInventoryNotFound() {
        when(inventoryRepository.findByBookBookId(1L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reduceStockOnOrder(1L, 50);
        });

        assertEquals("Inventory not found for the specified book ID: 1", exception.getMessage());
    }

//    @Test
//    public void testSaveInventory() {
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(modelMapper.map(inventoryDTO, Inventory.class)).thenReturn(inventory);
//        when(inventoryRepository.save(inventory)).thenReturn(inventory);
//        when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);
//
//        InventoryDTO result = inventoryService.saveInventory(1L, inventoryDTO);
//
//        assertNotNull(result);
//        assertEquals(inventoryDTO, result);
//    }

//    @Test
//    public void testSaveInventoryBookNotFound() {
//        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            inventoryService.saveInventory(1L, inventoryDTO);
//        });
//
//        assertEquals("Book not found for the specified ID: 1", exception.getMessage());
//    }
}
