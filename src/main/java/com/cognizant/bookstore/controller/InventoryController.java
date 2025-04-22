package com.cognizant.bookstore.controller;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.exceptions.*;
import com.cognizant.bookstore.service.InventoryService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/inventoryByName/{title}")
    public ResponseEntity<?> getInventoryByName(@PathVariable String title) {
        log.info("Received request to fetch inventory details for the book: {}", title);
        try {
            InventoryDTO inventory = inventoryService.getInventoryByName(title);
            log.info("Successfully fetched inventory for the book: {}", title);
            return ResponseEntity.ok(inventory);
        } catch (BookNotFoundException e) {
            log.warn("Book not found or inventory not available for the title: {}", title, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/admin/allInventories")
    public ResponseEntity<?> getAllInventories() {
        log.info("Received request to fetch all inventories.");
        try {
            List<InventoryDTO> inventories = inventoryService.getAllInventories();
            log.info("Successfully fetched {} inventories.", inventories.size());
            return ResponseEntity.ok(inventories);
        } catch (NoInventoriesFoundException e) {
            log.warn("No inventories found in the database.", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/reduceStock/{title}")
    public ResponseEntity<String> reduceStock(@PathVariable String title, @RequestParam int orderedQuantity) {
        log.info("Received request to reduce stock for the book: {} by {} units.", title, orderedQuantity);
        try {
            inventoryService.reduceStockOnOrder(title, orderedQuantity);
            log.info("Successfully reduced stock for the book: {}", title);
            return ResponseEntity.ok("Stock reduced successfully!");
        } catch (LowStockException e) {
            log.warn("Low stock alert for book: {}. {}", title, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InsufficientStockException e) {
            log.warn("Insufficient stock for book: {}. {}", title, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (BookNotFoundException e) {
            log.warn("Book not found while reducing stock for title: {}", title, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred while reducing stock for book: {}", title, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}

