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
import com.cognizant.bookstore.exceptions.BookNotFoundException;
import com.cognizant.bookstore.exceptions.InvalidOrderException;
import com.cognizant.bookstore.service.BookService;

import jakarta.validation.Valid;

@RestController
public class BookController {

	@Autowired
	private BookService bookService;

	@PostMapping("/save")
	public ResponseEntity<BookDTO> saveBooks(@Valid @RequestBody BookDTO book) {
		BookDTO savedBook = bookService.saveBook(book);
		return ResponseEntity.ok(savedBook);
	}

	@GetMapping("/getDetails")
	public ResponseEntity<List<BookDTO>> getBooks() {
		List<BookDTO> getBook = bookService.getBooks();
		return ResponseEntity.ok(getBook);
	}

	@PutMapping("/updateDetails/{title}")
	public ResponseEntity<BookDTO> updateBooks(@PathVariable String title, @Valid @RequestBody BookDTO bookDTO) {
		try {
			BookDTO updatedBook = bookService.updateBooks(title, bookDTO);
			return ResponseEntity.ok(updatedBook);
		} catch (BookNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (InvalidOrderException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@PatchMapping("/updateDetailsPatch/{title}")
	public ResponseEntity<BookDTO> updateBooksByPatch(@PathVariable String title, @Valid @RequestBody BookDTO bookDTO) {
		try {
			BookDTO updatedBook = bookService.updateBooksPatch(title, bookDTO);
			return ResponseEntity.ok(updatedBook);
		} catch (BookNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (InvalidOrderException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@DeleteMapping("/delete/{title}")
	public ResponseEntity<String> deleteBooks(@PathVariable String title) {
		try {
			bookService.deleteBook(title);
			return ResponseEntity.ok("Deleted Successfully");
		} catch (BookNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
