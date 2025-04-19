package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.exceptions.BookNotFoundException;
import com.cognizant.bookstore.exceptions.InsufficientStockException;
import com.cognizant.bookstore.exceptions.LowStockException;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.InventoryRepository;
import com.cognizant.bookstore.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
            throw new BookNotFoundException("Book not found or inventory not available for the title: " + title);
        }

        Inventory inventory = book.getInventory();
        return modelMapper.map(inventory, InventoryDTO.class);
    }

    @Override
    public List<InventoryDTO> getAllInventories() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(inventory -> modelMapper.map(inventory, InventoryDTO.class))
                .collect(Collectors.toList());
    }

    
    @Override
    @Transactional
    public void reduceStockOnOrder(String bookTitle, int orderedQuantity) {
        // Fetch book by title
        Book book = bookRepository.findByBookName(bookTitle);
        if (book == null || book.getInventory() == null) {
            throw new BookNotFoundException("Book not found or inventory not available for the title: " + bookTitle);
        }

        // Fetch inventory for the book
        Inventory inventory = inventoryRepository.findByBookBookId(book.getBookId());
        if (inventory == null) {
            throw new RuntimeException("Inventory not found for the book: " + bookTitle);
        }

        // Validate stock availability
        if (inventory.getStock() <= 0) {
            throw new InsufficientStockException("Stock is zero or less for the book: " + bookTitle);
        }
        if (inventory.getStock() < orderedQuantity) {
            throw new InsufficientStockException("Insufficient stock available for the book: " + bookTitle);
        }

        // Reduce stock
        inventory.setStock(inventory.getStock() - orderedQuantity);

        // Save updated inventory
        inventoryRepository.save(inventory);
    }


}
