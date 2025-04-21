package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.InventoryDTO;

import java.util.List;

public interface InventoryService {

    InventoryDTO getInventoryByName(String title);
    List<InventoryDTO> getAllInventories();
	void reduceStockOnOrder(String bookTitle, int orderedQuantity);
}
