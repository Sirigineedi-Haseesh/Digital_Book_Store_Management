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
import com.cognizant.bookstore.service.BookService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BookController {

	@Autowired
	private BookService bookService;

	@PostMapping("/admin/save")
    public ResponseEntity<?> saveBooks(@Valid @RequestBody BookDTO bookDTO) {
        log.info("Received request to save book: {}", bookDTO);

        try {
            // Save the book and log success
            BookDTO savedBook = bookService.saveBook(bookDTO);
            log.info("Book successfully saved");
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);

        } catch (DuplicateIsbnException e) {
            // Log duplicate ISBN issue
            log.error("Duplicate ISBN detected for book: {}, ISBN: {}", bookDTO.getTitle(), bookDTO.getIsbn());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (DuplicateTitleException e) {
            // Log duplicate title issue
            log.error("Duplicate title detected for book: {}", bookDTO.getTitle());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            // Log unexpected errors
            log.error("Unexpected error while saving book: {}", bookDTO, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

	@GetMapping("/getAllBooks")
    public ResponseEntity<?> getBooks() {
        log.info("Received request to fetch all books."); // Log when the request is received

        try {
            // Call the service to fetch books
            List<BookDTO> books = bookService.getBooks();

            // Log the success response
            log.info("Successfully fetched {} book(s) from the database.", books.size());
            return ResponseEntity.ok(books); // HTTP 200 OK

        } catch (BooksNotFoundException ex) {
            // Log when no books are found
            log.warn("No books found in the database. Returning HTTP 404.", ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // HTTP 404 Not Found
                                 .body(ex.getMessage());

        } catch (Exception ex) {
            // Log unexpected errors
            log.error("An unexpected error occurred while fetching books.", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500 Internal Server Error
                                 .body("An unexpected error occurred. Please try again later.");
        }
    }

	@PutMapping("/admin/updateDetails/{title}")
	public ResponseEntity<?> updateBooks(@PathVariable String title, @Valid @RequestBody BookDTO bookDTO) {
		try {
            // Call the service to update the book
            BookDTO updatedBook = bookService.updateBooks(title, bookDTO);
            // Log success
            log.info("Successfully updated book: {} with updated details: {}", title, bookDTO);
            return ResponseEntity.ok(updatedBook); // HTTP 200 OK
        } catch (BookNotFoundException e) {
            // Log book not found error
            log.warn("Book with title '{}' not found. Returning HTTP 404.", title, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DuplicateTitleException e) {
            // Log duplicate title error
            log.warn("Duplicate title detected: '{}'. Returning HTTP 400.", bookDTO.getTitle(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Log unexpected errors
            log.error("Unexpected error occurred while updating book with title: '{}'.", title, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An unexpected error occurred while updating the book.");
        }
    }

	@PatchMapping("/admin/updateDetailsPatch/{title}")
    public ResponseEntity<?> updateBooksByPatch(@PathVariable String title, @RequestBody BookDTO bookDTO) {
        log.info("Received request to partially update book with title: {}", title);

        try {
            // Call the service to perform the partial update
            BookDTO updatedBook = bookService.updateBooksPatch(title, bookDTO);

            // Log success
            log.info("Successfully updated book: {} with updated details: {}", title, bookDTO);
            return ResponseEntity.ok(updatedBook);

        } catch (BookNotFoundException e) {
            // Log book not found error
            log.warn("Book with title '{}' not found. Returning HTTP 404.", title, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (DuplicateTitleException e) {
            // Log duplicate title issue
            log.warn("Duplicate title detected for book: '{}'. Returning HTTP 400.", bookDTO.getTitle(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (DuplicateIsbnException e) {
            // Log duplicate ISBN issue
            log.warn("Duplicate ISBN detected for book: '{}'. Returning HTTP 400.", bookDTO.getIsbn(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            // Log unexpected errors
            log.error("An unexpected error occurred while updating book with title: '{}'.", title, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while updating the book.");
        }
    }

	@DeleteMapping("/admin/delete/{title}")
    public ResponseEntity<String> deleteBooks(@PathVariable String title) {
        log.info("Received request to delete book with title: {}", title); // Log request receipt

        try {
            // Call service to delete the book
            bookService.deleteBook(title);

            // Log successful deletion
            log.info("Book with title '{}' successfully deleted.", title);
            return ResponseEntity.ok("Book deleted successfully."); // HTTP 200 OK

        } catch (BookNotFoundException e) {
            // Log book not found error
            log.warn("Book with title '{}' not found. Returning HTTP 404.", title, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // HTTP 404 Not Found

        } catch (Exception e) {
            // Log unexpected errors
            log.error("An unexpected error occurred while deleting book with title: '{}'.", title, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
