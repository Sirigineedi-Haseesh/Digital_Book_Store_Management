package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.InventoryDTO;
import com.cognizant.bookstore.exceptions.InsufficientStockException;
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
            throw new RuntimeException("Inventory not found for the specified book title: " + title);
        }

        long id = book.getInventory().getInventoryId();
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

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
    public void reduceStockOnOrder(long bookId, int orderedQuantity) {
        Inventory inventory = inventoryRepository.findByBookBookId(bookId);

        if (inventory == null) {
            throw new RuntimeException("Inventory not found for the specified book ID: " + bookId);
        }

        if (inventory.getStock() <= 0) {
            throw new InsufficientStockException("Stock is zero or less for the book ID: " + bookId);
        }

        if (inventory.getStock() < orderedQuantity) {
            throw new InsufficientStockException("Insufficient stock available for the book ID: " + bookId);
        }
        // Reduce stock
        inventory.setStock(inventory.getStock() - orderedQuantity);

        // Save updated inventory
        inventoryRepository.save(inventory);

        // Check stock levels and send notification if stock falls below the threshold
        int minimumStock = 100; // Define your threshold
        if (inventory.getStock() <= minimumStock) {
            notifyBookService(bookId,inventory.getStock());
        }
    }

    private void notifyBookService(Long bookId, int remainingStock) {
        // Notify about low stock directly within the service
        System.out.println("Stock is low for Book ID: " + bookId + ". Remaining stock: " + remainingStock);

        // Add any custom logic for immediate handling of low stock if needed
    }

}
