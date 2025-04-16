package com.cognizant.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cognizant.bookstore.model.Book;


@Repository
public interface BookRepository extends JpaRepository<Book,Long>{
	
	@Query("SELECT B from Book B where B.title = :title")
	Book findByBookName(@Param("title") String title);
	Optional<Book> findByTitle(String title);

}
