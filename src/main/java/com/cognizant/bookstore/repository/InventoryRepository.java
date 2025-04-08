
package com.cognizant.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;

import com.cognizant.bookstore.model.Inventory;

import jakarta.transaction.Transactional;
public interface InventoryRepository extends JpaRepository<Inventory,Long>{
		
	
	@Query("SELECT i FROM Inventory i WHERE i.book.bookId = :bookId")
    //Inventory findByBookId(@Param("bookId") Long bookId);
	Inventory findByBookId(Long bookId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Inventory i WHERE i.book IS NULL")
	void deleteByNull();

}
