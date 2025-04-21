package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.bookstore.dto.BookDTO;
import com.cognizant.bookstore.exceptions.*;
import com.cognizant.bookstore.exceptions.InvalidOrderException;
import com.cognizant.bookstore.service.BookService;

import jakarta.validation.Valid;

@RestController
public class BookController {

	@Autowired
	private BookService bookService;

	@PostMapping("/admin/save")
	public ResponseEntity<?> saveBooks(@Valid @RequestBody BookDTO bookDTO) {
	    try {
	        // Save the book and return success response
	        BookDTO savedBook = bookService.saveBook(bookDTO);
	        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
	    } catch (DuplicateIsbnException e) {
	        // Handle duplicate title or other validation-related issues
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (DuplicateTitleException e) {
	        // Handle duplicate title or other validation-related issues
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }catch (Exception e) {
	        // Handle unexpected server errors
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
	    }
	}

	@GetMapping("/getAllBooks")
	public ResponseEntity<?> getBooks() {
	    try {
	        List<BookDTO> books = bookService.getBooks();
	        return ResponseEntity.ok(books); // HTTP 200 OK
	    } catch (BooksNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND) // HTTP 404 Not Found
	                             .body(ex.getMessage());
	    }
	}



	@PutMapping("/admin/updateDetails/{title}")
	public ResponseEntity<?> updateBooks(@PathVariable String title, @Valid @RequestBody BookDTO bookDTO) {
		try {
			BookDTO updatedBook = bookService.updateBooks(title, bookDTO);
			return ResponseEntity.ok(updatedBook);
		} catch (BooksNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (DuplicateTitleException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PatchMapping("/admin/updateDetailsPatch/{title}")
	public ResponseEntity<?> updateBooksByPatch(@PathVariable String title,@RequestBody BookDTO bookDTO) {
		try {
			BookDTO updatedBook = bookService.updateBooksPatch(title, bookDTO);
			return ResponseEntity.ok(updatedBook);
		} catch (BooksNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (InvalidOrderException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}catch (DuplicateTitleException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/admin/delete/{title}")
	public ResponseEntity<String> deleteBooks(@PathVariable String title) {
		try {
			bookService.deleteBook(title);
			return ResponseEntity.ok("Deleted Successfully");
		} catch (BooksNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
