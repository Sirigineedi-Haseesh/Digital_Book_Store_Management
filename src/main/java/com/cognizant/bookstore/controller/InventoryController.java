package com.cognizant.bookstore.controller;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.exceptions.*;
import com.cognizant.bookstore.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/admin/inventoryByName/{title}")
    public ResponseEntity<?> getInventoryByName(@PathVariable String title) {
        try {
            InventoryDTO inventory = inventoryService.getInventoryByName(title);
            return ResponseEntity.ok(inventory);
        } catch (BooksNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/admin/allInventories")
    public ResponseEntity<?> getAllInventories() {
        try {
            // Call the service to get all inventories
            List<InventoryDTO> inventories = inventoryService.getAllInventories();
            return ResponseEntity.ok(inventories);
        } catch (NoInventoriesFoundException e) {
            // Handle the user-defined exception
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/reduceStock/{title}")
    public ResponseEntity<String> reduceStock(@PathVariable String title, @RequestParam int orderedQuantity) {
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

