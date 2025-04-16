package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

	@Mock
	private InventoryRepository inventoryRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private InventoryService inventoryService;

	private Inventory inventory;
	private InventoryDTO inventoryDTO;
	private Book book;

	@BeforeEach
	public void setUp() {
		// Mock Inventory object
		inventory = new Inventory();
		inventory.setInventoryId(1L);
		inventory.setStock(10);

		// Mock Book object
		book = new Book();
		book.setBookId(1L);
		book.setAuthorName("Test Book");
		inventory.setBook(book);

		// Mock InventoryDTO object
		inventoryDTO = new InventoryDTO();
		inventoryDTO.setInventoryId(1L);
		inventoryDTO.setStock(10);
		inventoryDTO.setBook(book);
	}

	@Test
	public void testGetInventoryByName() {
		// Mocking the book object with inventory
		book.setInventory(inventory); // Associate the mocked inventory with the mocked book

		// Mock repository and mapper behavior
		when(bookRepository.findByBookName("Test Book")).thenReturn(book);
		when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
		when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

		// Call the service method
		InventoryDTO result = inventoryService.getInventoryByName("Test Book");

		// Assertions
		assertNotNull(result);
		assertEquals(inventoryDTO, result);

		// Verify interactions with mocks
		verify(bookRepository, times(1)).findByBookName("Test Book");
		verify(inventoryRepository, times(1)).findById(1L);
		verify(modelMapper, times(1)).map(inventory, InventoryDTO.class);
	}

	@Test
	public void testGetInventoryByNameNotFound() {
		// Mock book retrieval to return null
		when(bookRepository.findByBookName("Non-Existent Book")).thenReturn(null);

		// Expect a RuntimeException when calling the service method
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			inventoryService.getInventoryByName("Non-Existent Book");
		});

		// Assert exception message
		assertEquals("Inventory not found for the specified book title: Non-Existent Book", exception.getMessage());

		// Verify interactions
		verify(bookRepository, times(1)).findByBookName("Non-Existent Book");
		verifyNoInteractions(inventoryRepository); // Should not interact with InventoryRepository
	}

	@Test
	public void testDeleteInventory() {
		when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
		when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

		InventoryDTO result = inventoryService.deleteInventory(1L);

		assertNotNull(result);
		assertEquals(inventoryDTO, result);
		verify(inventoryRepository, times(1)).delete(inventory);
	}

	@Test
	public void testDeleteInventoryNotFound() {
		when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			inventoryService.deleteInventory(1L);
		});

		assertEquals("Inventory not found for the specified ID: 1", exception.getMessage());
	}

	@Test
	public void testGetAllInventories() {
		when(inventoryRepository.findAll()).thenReturn(Arrays.asList(inventory));
		when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

		List<InventoryDTO> result = inventoryService.getAllInventories();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(inventoryDTO, result.get(0));
	}

	@Test
	public void testUpdateStock() {
		when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
		when(inventoryRepository.save(inventory)).thenReturn(inventory);
		when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

		InventoryDTO newStock = new InventoryDTO();
		newStock.setStock(20);

		InventoryDTO result = inventoryService.updateStock(1L, newStock);

		assertNotNull(result);
		assertEquals(20, inventory.getStock());
		verify(inventoryRepository, times(1)).save(inventory);
	}

	@Test
	public void testUpdateStockNotFound() {
		when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			InventoryDTO newStock = new InventoryDTO();
			newStock.setStock(20);
			inventoryService.updateStock(1L, newStock);
		});

		assertEquals("Inventory not found for the specified ID: 1", exception.getMessage());
	}

	@Test
	public void testReduceStockOnOrder() {
		when(inventoryRepository.findByBookBookId(1L)).thenReturn(inventory);

		inventoryService.reduceStockOnOrder(1L, 5);

		assertEquals(5, inventory.getStock());
		verify(inventoryRepository, times(1)).save(inventory);
	}

	@Test
	public void testReduceStockOnOrderInsufficientStock() {
		when(inventoryRepository.findByBookBookId(1L)).thenReturn(inventory);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			inventoryService.reduceStockOnOrder(1L, 15);
		});

		assertEquals("Insufficient stock available for the book ID: 1", exception.getMessage());
	}

	@Test
	public void testSaveInventory() {
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(modelMapper.map(inventoryDTO, Inventory.class)).thenReturn(inventory);
		when(inventoryRepository.save(inventory)).thenReturn(inventory);
		when(modelMapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

		InventoryDTO result = inventoryService.saveInventory(1L, inventoryDTO);

		assertNotNull(result);
		assertEquals(inventoryDTO, result);
		verify(inventoryRepository, times(1)).save(inventory);
	}

	@Test
	public void testSaveInventoryBookNotFound() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			inventoryService.saveInventory(1L, inventoryDTO);
		});

		assertEquals("Book not found for the specified ID: 1", exception.getMessage());
	}
}
