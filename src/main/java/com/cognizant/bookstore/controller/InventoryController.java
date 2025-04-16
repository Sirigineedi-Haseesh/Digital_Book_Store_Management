package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.service.InventoryService;

@RestController

@RequestMapping("/api/inventory")

public class InventoryController {

	@Autowired

	private InventoryService inventoryService;

	/**
	 * 
	 * Fetch inventory by book name.
	 *
	 * 
	 * 
	 * @param title the book title
	 * 
	 * @return inventory details or 404 status if not found
	 * 
	 */

	@GetMapping("/inventoryByName/{title}")

	public ResponseEntity<InventoryDTO> getInventoryByName(@PathVariable String title) {

		try {

			InventoryDTO inventory = inventoryService.getInventoryByName(title);

			return ResponseEntity.ok(inventory);

		} catch (RuntimeException e) {

			// Return 404 if inventory is not found

			return ResponseEntity.status(HttpStatus.NOT_FOUND)

					.body(null);

		}

	}

	/**
	 * 
	 * Reduce stock by book ID and order quantity.
	 *
	 * 
	 * 
	 * @param bookId          the book ID
	 * 
	 * @param orderedQuantity quantity to reduce
	 * 
	 * @return confirmation message or 400 status if stock is insufficient
	 * 
	 */

	@PatchMapping("/reduceStock/{bookId}")

	public ResponseEntity<String> reduceStock(@PathVariable("bookId") Long bookId, @RequestParam int orderedQuantity) {

		try {

			inventoryService.reduceStockOnOrder(bookId, orderedQuantity);

			return ResponseEntity.ok("Stock reduced successfully!");

		} catch (RuntimeException e) {

			// Return 400 if the request is invalid (e.g., insufficient stock)

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)

					.body(e.getMessage());

		}

	}

	/**
	 * 
	 * Save inventory and associate it with a book.
	 *
	 * 
	 * 
	 * @param bookId       the book ID
	 * 
	 * @param inventoryDTO inventory details to save
	 * 
	 * @return saved inventory details or 400 status if the request is invalid
	 * 
	 */

	@PostMapping("/saveInventory/{bookId}")

	public ResponseEntity<InventoryDTO> saveInventory(@PathVariable long bookId,
			@RequestBody InventoryDTO inventoryDTO) {

		try {

			InventoryDTO savedInventory = inventoryService.saveInventory(bookId, inventoryDTO);

			return ResponseEntity.status(HttpStatus.CREATED)

					.body(savedInventory);

		} catch (RuntimeException e) {

			// Return 400 if there's an issue saving the inventory

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)

					.body(null);

		}

	}

	/**
	 * 
	 * Delete inventory by ID.
	 *
	 * 
	 * 
	 * @param id the inventory ID
	 * 
	 * @return confirmation message or 404 status if not found
	 * 
	 */

	@DeleteMapping("/delete/{id}")

	public ResponseEntity<String> deleteInventory(@PathVariable Long id) {

		try {

			inventoryService.deleteInventory(id);

			return ResponseEntity.ok("Inventory deleted successfully!");

		} catch (RuntimeException e) {

			// Return 404 if inventory is not found

			return ResponseEntity.status(HttpStatus.NOT_FOUND)

					.body("Inventory not found");

		}

	}

	/**
	 * 
	 * Get all inventories.
	 *
	 * 
	 * 
	 * @return list of all inventories or 204 status if the list is empty
	 * 
	 */

	@GetMapping("/allInventories")

	public ResponseEntity<List<InventoryDTO>> getAllInventories() {

		List<InventoryDTO> inventories = inventoryService.getAllInventories();

		if (!inventories.isEmpty()) {

			return ResponseEntity.ok(inventories);

		} else {

			// Return 204 if no inventories are found

			return ResponseEntity.status(HttpStatus.NO_CONTENT)

					.body(null);

		}

	}

}
