package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
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
import com.cognizant.bookstore.service.BookService;


@RestController
public class BookController {
	@Autowired
	private BookService bookService;
	@PostMapping("/save")
	public ResponseEntity<BookDTO> saveBooks(@RequestBody BookDTO book){
		BookDTO savedBook = bookService.saveBook(book);
		return ResponseEntity.ok(savedBook);
	}
	
	@GetMapping("/getDetails")
	public ResponseEntity<List<BookDTO>> getBooks(){
		List<BookDTO> getBook = bookService.getBooks();
		return ResponseEntity.ok(getBook);
	}
	
//	@GetMapping("/getByBookName/{title}")
//	public ResponseEntity<List<BookDTO>> getByBookNames(@PathVariable String title){
//
//		List<BookDTO> getBook = bookService.getByBookName(title);
//		return ResponseEntity.ok(getBook);
//	}
	
	@PutMapping("/updateDetails/{title}")
	public ResponseEntity<BookDTO> updateBooks(@PathVariable String title , @RequestBody BookDTO bookDTO ){
		BookDTO updatedBook = bookService.updateBooks(title,bookDTO);
		return ResponseEntity.ok(updatedBook);
	}
	
	@PatchMapping("/updateDetailsPatch/{title}")
	public ResponseEntity<BookDTO> updateBooksByPatch(@PathVariable String title, @RequestBody BookDTO bookDTO) {
	    BookDTO updatedBook = bookService.updateBooksPatch(title, bookDTO);
	    return ResponseEntity.ok(updatedBook);
	}
	@DeleteMapping("/delete/{title}")
	public ResponseEntity<String> deleteBooks(@PathVariable String title) {
		bookService.deleteBook(title);
		return ResponseEntity.ok("Deleted Sucessfully");
		
	}
}
