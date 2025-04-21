package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.exceptions.*;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.InventoryRepository;
import com.cognizant.bookstore.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryServiceImp implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BookRepository bookRepository;
 
    @Override
    public InventoryDTO getInventoryByName(String title) {
        Book book = bookRepository.findByBookName(title);

        if (book == null || book.getInventory() == null) {
        	log.error("Inventory Not Found for Book "+title);
            throw new BookNotFoundException("Book not found or inventory not available for the title: " + title);
        }

        Inventory inventory = book.getInventory();
        return modelMapper.map(inventory, InventoryDTO.class);
    }

    @Override
    public List<InventoryDTO> getAllInventories() {
        List<Inventory> inventories = inventoryRepository.findAll();
        if(inventories.isEmpty()) {
        	throw new NoInventoriesFoundException("No Inventories are Present");
        }
        return inventories.stream()
                .map(inventory -> modelMapper.map(inventory, InventoryDTO.class))
                .collect(Collectors.toList());
    }

    
    @Override
    @Transactional
    public void reduceStockOnOrder(String bookTitle, int orderedQuantity) {
        Book book = bookRepository.findByBookName(bookTitle);
        if (book == null || book.getInventory() == null) {
        	log.warn("No Book Found with title "+bookTitle);
            throw new BookNotFoundException("Book not found or inventory not available for the title: " + bookTitle);
        }
 
        Inventory inventory = inventoryRepository.findByBookBookId(book.getBookId());
        if (inventory == null) {
        	log.warn("No Inventory Found for Book "+bookTitle);
            throw new InventoryNotFoundException("Inventory not found for the book: " + bookTitle);
        }
 
        if (inventory.getStock() < orderedQuantity) {
            throw new InsufficientStockException("Insufficient stock available for the book: " + bookTitle);
        }
 
        inventory.setStock(inventory.getStock() - orderedQuantity);
 
        // Check and notify low stock
        if (inventory.getStock() <=50) {
        	log.warn("Low stock alert for book '{}'. Current stock: {}", bookTitle, inventory.getStock());
        }
 
        inventoryRepository.save(inventory);
        log.info("Stock Reduced Suceessfully");
    }
}
