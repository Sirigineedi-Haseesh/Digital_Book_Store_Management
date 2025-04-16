package com.cognizant.bookstore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.dto.BookDTO;
import com.cognizant.bookstore.exceptions.BookNotFoundException;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;

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
		log.info("Saving new book with title: {}", bookDTO.getTitle());
		Book book = modelMapper.map(bookDTO, Book.class);

		// First, save the Book to generate its ID
		Book savedBook = bookRepository.save(book);
		log.info("Book saved with ID: {}", savedBook.getBookId());

		// Now, if Inventory exists, associate the saved book and persist Inventory
		if (bookDTO.getInventory() != null) {
			Inventory inventory = modelMapper.map(bookDTO.getInventory(), Inventory.class);
			inventory.setBook(savedBook); // Assign the saved Book
			Inventory savedInventory = inventoryRepository.save(inventory); // Persist Inventory
			savedBook.setInventory(savedInventory); // Ensure Book has updated reference
			log.info("Inventory saved with ID: {}", savedInventory.getInventoryId());
		}

		return modelMapper.map(savedBook, BookDTO.class);
	}

	public List<BookDTO> getBooks() {
		log.info("Fetching all books");
		List<Book> books = bookRepository.findAll();
		log.info("Total books found: {}", books.size());
		return books.stream().map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
	}

	public BookDTO updateBooks(String title, BookDTO bookDTO) {
	    log.info("Updating book with title: {}", title);
	    Book book = bookRepository.findByBookName(title);
	    if (book == null) {
	        log.error("Book not found with title: {}", title);
	        throw new BookNotFoundException("Book not found with title: " + title);
	    }
	    long originalId = book.getBookId();
	    modelMapper.map(bookDTO, book);
	    book.setBookId(originalId);
	    Book updatedBook = bookRepository.save(book);
	    log.info("Book updated with ID: {}", updatedBook.getBookId());

	    // Ensure inventory exists before updating stock
	    Inventory inventory = inventoryRepository.findByBookBookId(book.getBookId());
	    if (inventory == null) {
	        inventory = new Inventory(); // Create a new inventory if it doesn't exist
	        inventory.setBook(book); // Link the inventory to the book
	    }
	    if (book.getInventory() != null) {
	        inventory.setStock(book.getInventory().getStock());
	    }
	    inventoryRepository.save(inventory);
	    book.setInventory(inventory); // Ensure book has an inventory reference
	    log.info("Inventory updated for book ID: {}", book.getBookId());

	    return modelMapper.map(updatedBook, BookDTO.class);
	}


	public BookDTO updateBooksPatch(String title, BookDTO bookDTO) {
		log.info("Patching book with title: {}", title);
		Book book = bookRepository.findByBookName(title);

		if (book == null) {
			log.error("Book not found with title: {}", title);
			throw new BookNotFoundException("Book not found with title: " + title);
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
		log.info("Book patched with ID: {}", updatedBook.getBookId());

		return modelMapper.map(updatedBook, BookDTO.class);
	}

	public void deleteBook(String title) {
		log.info("Deleting book with title: {}", title);
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
