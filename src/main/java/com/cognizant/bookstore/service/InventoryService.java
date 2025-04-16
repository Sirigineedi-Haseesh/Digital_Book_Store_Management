package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.InventoryDTO;

import java.util.List;

public interface InventoryService {

    InventoryDTO getInventoryByName(String title);

//    InventoryDTO deleteInventory(Long id);

    List<InventoryDTO> getAllInventories();

    void reduceStockOnOrder(long bookId, int orderedQuantity);

//    InventoryDTO saveInventory(long bookId, InventoryDTO inventoryDTO);

}
