package com.cognizant.bookstore.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import com.cognizant.bookstore.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory,Long>{
	Inventory findByBookBookId(Long bookId);
}

 