package com.cognizant.bookstore.controller;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.exceptions.BookNotFoundException;
import com.cognizant.bookstore.exceptions.InsufficientStockException;
import com.cognizant.bookstore.exceptions.LowStockException;
import com.cognizant.bookstore.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/inventoryByName/{title}")
    public ResponseEntity<InventoryDTO> getInventoryByName(@PathVariable String title) {
        try {
            InventoryDTO inventory = inventoryService.getInventoryByName(title);
            return ResponseEntity.ok(inventory);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/allInventories")
    public ResponseEntity<List<InventoryDTO>> getAllInventories() {
        List<InventoryDTO> inventories = inventoryService.getAllInventories();
        if (!inventories.isEmpty()) {
            return ResponseEntity.ok(inventories);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
    }

    @PatchMapping("/reduceStock/{title}")
    public ResponseEntity<String> reduceStock(@PathVariable("title") String title, @RequestParam int orderedQuantity) {
        try {
            inventoryService.reduceStockOnOrder(title, orderedQuantity);
            return ResponseEntity.ok("Stock reduced successfully!");
        } catch (LowStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

}

