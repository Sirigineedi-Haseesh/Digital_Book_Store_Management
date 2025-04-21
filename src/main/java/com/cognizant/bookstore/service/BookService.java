package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.BookDTO;
import com.cognizant.bookstore.exceptions.*;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;

import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class BookService {
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	private ModelMapper modelMapper;
	
	
	public BookDTO saveBook(BookDTO bookDTO) {
		if (bookRepository.existsByTitle(bookDTO.getTitle())) {
			log.error("Book with Title exists");
	        throw new DuplicateTitleException("Book with title '" + bookDTO.getTitle() + "' already exists");
	    }
		if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
			log.error("Book with ISBN exists");
	        throw new DuplicateIsbnException("Book with ISBN '" + bookDTO.getIsbn() + "' already exists ISBN Must Be Unique");
	    }
		//Convert from BookDTO to Book Class
		Book book = modelMapper.map(bookDTO, Book.class);
		// First, save the Book to generate its ID
		Book savedBook = bookRepository.save(book);
		//get the inventory from book
		Inventory inventory = book.getInventory();
		//save the book to the inventory so that it will reflect
		inventory.setBook(savedBook);
		//save the inventory to create a row
		inventoryRepository.save(inventory);
		//set the inventory to make the connection
		savedBook.setInventory(inventory);
		log.info("Book Saved Sucessfully");
		return modelMapper.map(savedBook, BookDTO.class);
	}
 
	
	
	@Transactional   
	public List<BookDTO> getBooks() {
		List<Book> books = bookRepository.findAll();
		if (books.isEmpty()) {
	        log.error("No books found in the database.");
	        throw new BooksNotFoundException("No books found");
	    }
		log.info("Total books found: {}", books.size());
		return books.stream().map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
	}

	public BookDTO updateBooks(String title, BookDTO bookDTO) {
	    Book books = bookRepository.findByBookName(title);
	    if (books == null) {
	        throw new BookNotFoundException("Book not found with title: " + title);
	    }
	    if(bookRepository.existsByTitle(bookDTO.getTitle())){
	    	throw new DuplicateTitleException("Book With: "+bookDTO.getTitle()+" Is Aldready In the Application");
	    }
	    long originalId = books.getBookId();
	    long originalInId = books.getInventory().getInventoryId();
	    modelMapper.map(bookDTO, books);
	    books.setBookId(originalId);
	    Inventory inventory = books.getInventory();
	    inventory.setStock(books.getInventory().getStock());
	    inventory.setBook(books);
	    inventory.setInventoryId(originalInId);
	    Book updatedBook = bookRepository.save(books);
	    log.info("Updated Book Details of Book Name "+title);
	    return modelMapper.map(updatedBook, BookDTO.class);
	}

	public BookDTO updateBooksPatch(String title, BookDTO bookDTO) {
		Book book = bookRepository.findByBookName(title);
		if (book == null) {
			throw new BookNotFoundException("Book not found with title: " + title);
		}
		if(bookRepository.existsByTitle(bookDTO.getTitle())){
	    	throw new DuplicateTitleException("Book With: "+bookDTO.getTitle()+" Is Aldready In the Application");
	    }
		// Update only fields that are not null in bookDTO
		if (bookDTO.getIsbn() != null)
			book.setIsbn(bookDTO.getIsbn());
		if (bookDTO.getCategory() != null)
			book.setCategory(bookDTO.getCategory());
		if (bookDTO.getPrice() != 0)
			book.setPrice(bookDTO.getPrice());
		if (bookDTO.getAuthorName() != null)
			book.setAuthorName(bookDTO.getAuthorName());
		if (bookDTO.getImages() != null)
			book.setImages(bookDTO.getImages());

		// Ensure inventory exists before updating stock
		Inventory inventory = book.getInventory();
		if (inventory == null) {
			inventory = new Inventory(); // Create a new inventory if it doesn't exist
			inventory.setBook(book); // Link the inventory to the book
		}

		if (bookDTO.getInventory() != null) {
			inventory.setStock(inventory.getStock() + bookDTO.getInventory().getStock());
		}
		inventoryRepository.save(inventory);
		book.setInventory(inventory); // Ensure book has an inventory reference
		// Save the updated book
		Book updatedBook = bookRepository.save(book);

		return modelMapper.map(updatedBook, BookDTO.class);
	}

	public void deleteBook(String title) {
		Book book = bookRepository.findByBookName(title);

		if (book == null) {
			log.error("Book not found with title: {}", title);
			throw new BookNotFoundException("Book not found with title: " + title);
		}

		// Delete the book (Inventory is deleted automatically via Cascade)
		bookRepository.delete(book);
		log.info("Book deleted with title: {}", title);
	}
}
