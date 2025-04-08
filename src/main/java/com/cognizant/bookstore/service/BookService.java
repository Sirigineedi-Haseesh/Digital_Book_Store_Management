package com.cognizant.bookstore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.dto.BookDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;


@Service
public class BookService {
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	private ModelMapper modelMapper;
	public BookDTO saveBook(BookDTO bookDTO) {
		Book book = modelMapper.map(bookDTO, Book.class);

	    // First, save the Book to generate its ID
	    Book savedBook = bookRepository.save(book);
	    // Now, if Inventory exists, associate the saved book and persist Inventory
	    if (bookDTO.getInventory() != null) {
	        Inventory inventory = modelMapper.map(bookDTO.getInventory(), Inventory.class);
	        inventory.setBook(savedBook); // Assign the saved Book
	        Inventory savedInventory = inventoryRepository.save(inventory); // Persist Inventory
	        savedBook.setInventory(savedInventory); // Ensure Book has updated reference
	    }
	    return modelMapper.map(savedBook, BookDTO.class);
	}
	
	public List<BookDTO> getBooks() {
		List<Book> books = bookRepository.findAll();
		return books.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
	}

	
	public BookDTO updateBooks(String title, BookDTO bookDTO) {
	    Book books = bookRepository.findByBookName(title);
	    if (books == null) {
	        throw new RuntimeException("Book not found with title: " + title);
	    }
	    long originalId = books.getBookId();
	    modelMapper.map(bookDTO, books);
	    books.setBookId(originalId);
	    Book updatedBook = bookRepository.save(books);
	    
	    Inventory inventory = inventoryRepository.findByBookId(books.getBookId());
	    inventory.setStock(books.getInventory().getStock());
	    inventory.setBook(books);
	    inventoryRepository.save(inventory);
	    inventoryRepository.deleteByNull();
	    return modelMapper.map(updatedBook, BookDTO.class);
	    
	}
	
	public BookDTO updateBooksPatch(String title, BookDTO bookDTO) {
	    Book book = bookRepository.findByBookName(title);

	    if (book == null) {
	        throw new RuntimeException("Book not found with title: " + title);
	    }
	    // Update only fields that are not null in bookDTO
	    if (bookDTO.getIsbn() != null) book.setIsbn(bookDTO.getIsbn());
	    if (bookDTO.getCategory() != null) book.setCategory(bookDTO.getCategory());
	    if (bookDTO.getPrice() != 0) book.setPrice(bookDTO.getPrice());
	    if (bookDTO.getAuthorName() != null) book.setAuthorName(bookDTO.getAuthorName());
	    if (bookDTO.getImages() != null) book.setImages(bookDTO.getImages());

	    // Ensure inventory exists before updating stock
	    Inventory inventory = book.getInventory();

	    if (inventory == null) {
	        inventory = new Inventory();  // Create a new inventory if it doesn't exist
	        inventory.setBook(book);      // Link the inventory to the book
	    }

	    if (bookDTO.getInventory() != null) {
	        inventory.setStock(bookDTO.getInventory().getStock());
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
	        throw new RuntimeException("Book not found with title: " + title);
	    }

	    // Delete the book (Inventory is deleted automatically via Cascade)
	    bookRepository.delete(book);
	}

}
