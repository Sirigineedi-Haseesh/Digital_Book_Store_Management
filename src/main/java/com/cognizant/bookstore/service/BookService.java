package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.BookDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Save a new book.
     *
     * @param bookDTO The book details in DTO format.
     * @return The saved book in DTO format.
     */
    public BookDTO saveBook(BookDTO bookDTO) {
        Book book = modelMapper.map(bookDTO, Book.class);
        Book savedBook = bookRepository.save(book);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    /**
     * Retrieve all books.
     *
     * @return A list of all books in DTO format.
     */
    public List<BookDTO> getBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Update a book by title.
     *
     * @param title   The title of the book to update.
     * @param bookDTO The new details for the book.
     * @return The updated book in DTO format.
     */
    public BookDTO updateBooks(String title, BookDTO bookDTO) {
        // Fetch the book by title
        Book book = bookRepository.findByBookName(title);
        if (book == null) {
            throw new RuntimeException("Book not found with title: " + title);
        }

        // Preserve the original book ID
        long originalId = book.getBookId();

        // Map the DTO to the entity
        modelMapper.map(bookDTO, book);
        book.setBookId(originalId); // Ensure the original ID is preserved

        // Save the updated book
        Book updatedBook = bookRepository.save(book);

        // Update inventory associated with the book
        Inventory inventory = inventoryRepository.findByBookBookId(updatedBook.getBookId());
        if (inventory != null && bookDTO.getInventory() != null) {
            inventory.setStock(bookDTO.getInventory().getStock());
            inventory.setBook(updatedBook);
            inventoryRepository.save(inventory);
        }

        // Return the updated book as a DTO
        return modelMapper.map(updatedBook, BookDTO.class);
    }


    /**
     * Partially update a book by title.
     *
     * @param title   The title of the book to partially update.
     * @param bookDTO The patch details for the book.
     * @return The partially updated book in DTO format.
     */
    public BookDTO updateBooksPatch(String title, BookDTO bookDTO) {
        Book existingBook = bookRepository.findByBookName(title);
        if (existingBook == null) {
            throw new RuntimeException("Book not found with title: " + title);
        }

        // Apply patch updates to the existing book entity
        if (bookDTO.getAuthorName() != null) {
            existingBook.setAuthorName(bookDTO.getAuthorName());
        }
        if (bookDTO.getCategory() != null) {
            existingBook.setCategory(bookDTO.getCategory());
        }
        if (bookDTO.getIsbn() != null) {
            existingBook.setIsbn(bookDTO.getIsbn());
        }
        if (bookDTO.getPrice() > 0) {
            existingBook.setPrice(bookDTO.getPrice());
        }
        if (bookDTO.getImages() != null) {
            existingBook.setImages(bookDTO.getImages());
        }

        // Save the updated book
        Book updatedBook = bookRepository.save(existingBook);

        // Handle inventory updates if present
        Optional.ofNullable(bookDTO.getInventory())
                .ifPresent(dtoInventory -> {
                    Inventory existingInventory = inventoryRepository.findByBookBookId(updatedBook.getBookId());
                    if (existingInventory != null) {
                        existingInventory.setStock(dtoInventory.getStock());
                        inventoryRepository.save(existingInventory);
                    }
                });

        return modelMapper.map(updatedBook, BookDTO.class);
    }

    /**
     * Delete a book by title.
     *
     * @param title The title of the book to delete.
     */
    public void deleteBook(String title) {
        Book book = bookRepository.findByBookName(title);
        if (book == null) {
            throw new RuntimeException("Book not found with title: " + title);
        }
        bookRepository.delete(book);
    }
}
