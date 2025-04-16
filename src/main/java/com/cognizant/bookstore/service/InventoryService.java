package com.cognizant.bookstore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;

import jakarta.transaction.Transactional;

@Service

public class InventoryService {

	@Autowired

	private InventoryRepository inventoryRepository;

	@Autowired

	private ModelMapper modelMapper;

	@Autowired

	private BookRepository bookRepository;

	/**
	 * 
	 * Fetch inventory by book name.
	 *
	 * 
	 * 
	 * @param title the name of the book
	 * 
	 * @return inventory details in DTO format
	 * 
	 */

	public InventoryDTO getInventoryByName(String title) {

		Book book = bookRepository.findByBookName(title);

		if (book == null || book.getInventory() == null) {

			throw new RuntimeException("Inventory not found for the specified book title: " + title);

		}

		long id = book.getInventory().getInventoryId();

		Inventory inventory = inventoryRepository.findById(id)

				.orElseThrow(() -> new RuntimeException("Inventory not found"));

		return modelMapper.map(inventory, InventoryDTO.class);

	}

	/**
	 * 
	 * Delete inventory by ID.
	 *
	 * 
	 * 
	 * @param id the inventory ID
	 * 
	 * @return deleted inventory details in DTO format
	 * 
	 */

	@Transactional

	public InventoryDTO deleteInventory(Long id) {

		Inventory inventory = inventoryRepository.findById(id)

				.orElseThrow(() -> new RuntimeException("Inventory not found for the specified ID: " + id));

		inventoryRepository.delete(inventory);

		return modelMapper.map(inventory, InventoryDTO.class);

	}

	/**
	 * 
	 * Retrieve all inventories.
	 *
	 * 
	 * 
	 * @return list of all inventories in DTO format
	 * 
	 */

	public List<InventoryDTO> getAllInventories() {

		List<Inventory> inventories = inventoryRepository.findAll();

		return inventories.stream()

				.map(inventory -> modelMapper.map(inventory, InventoryDTO.class))

				.collect(Collectors.toList());

	}

	/**
	 * 
	 * Update stock for a given inventory ID.
	 *
	 * 
	 * 
	 * @param id       the inventory ID
	 * 
	 * @param newStock updated stock details
	 * 
	 * @return updated inventory in DTO format
	 * 
	 */

	public InventoryDTO updateStock(Long id, InventoryDTO newStock) {

		Inventory inventory = inventoryRepository.findById(id)

				.orElseThrow(() -> new RuntimeException("Inventory not found for the specified ID: " + id));

		inventory.setStock(newStock.getStock());

		Inventory updatedInventory = inventoryRepository.save(inventory);

		return modelMapper.map(updatedInventory, InventoryDTO.class);

	}

	/**
	 * 
	 * Reduce stock when an order is placed.
	 *
	 * 
	 * 
	 * @param bookId          the book ID
	 * 
	 * @param orderedQuantity the quantity ordered
	 * 
	 */

	@Transactional

	public void reduceStockOnOrder(long bookId, int orderedQuantity) {

		// Fetch the inventory associated with the book ID

		Inventory inventory = inventoryRepository.findByBookBookId(bookId);

		if (inventory == null) {

			throw new RuntimeException("Inventory not found for the specified book ID: " + bookId);

		}

		// Check if there is sufficient stock to fulfill the order

		if (inventory.getStock() < orderedQuantity) {

			throw new RuntimeException("Insufficient stock available for the book ID: " + bookId);

		}

		// Reduce the stock based on the order quantity

		inventory.setStock(inventory.getStock() - orderedQuantity);

		// Save the updated inventory back to the database

		inventoryRepository.save(inventory);

	}

	/**
	 * 
	 * Save inventory and associate with a book.
	 *
	 * 
	 * 
	 * @param id           the book ID
	 * 
	 * @param inventoryDTO inventory details in DTO format
	 * 
	 * @return saved inventory in DTO format
	 * 
	 */

	public InventoryDTO saveInventory(long id, InventoryDTO inventoryDTO) {

		Inventory inventory = modelMapper.map(inventoryDTO, Inventory.class);

		Book book = bookRepository.findById(id)

				.orElseThrow(() -> new RuntimeException("Book not found for the specified ID: " + id));

		inventory.setBook(book);

		Inventory savedInventory = inventoryRepository.save(inventory);

		return modelMapper.map(savedInventory, InventoryDTO.class);

	}

}
