package com.cognizant.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cognizant.bookstore.model.Book;


@Repository
public interface BookRepository extends JpaRepository<Book,Long>{
	
	@Query("SELECT B from Book B where B.title = :title")
	Book findByBookName(@Param("title") String title);
	
}
