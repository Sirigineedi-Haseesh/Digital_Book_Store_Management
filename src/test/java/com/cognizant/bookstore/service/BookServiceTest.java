package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.cognizant.bookstore.dto.BookDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookDTO bookDTO;
    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        book = new Book();
        book.setBookId(1L);
        book.setTitle("Book Title");

        inventory = new Inventory();
        inventory.setStock(10);
        inventory.setBook(book);

        bookDTO = new BookDTO();
        bookDTO.setBookId(1L);
        bookDTO.setTitle("Book Title");
    }

    @Test
    public void testSaveBook() {
        when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO savedBook = bookService.saveBook(bookDTO);

        assertNotNull(savedBook);
        assertEquals(bookDTO, savedBook);
    }

    @Test
    public void testGetBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        List<BookDTO> books = bookService.getBooks();

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals(bookDTO, books.get(0));
    }

    @Test
    public void testUpdateBooks() {
        // Mock repository call for fetching the book by title
        when(bookRepository.findByBookName("Book Title")).thenReturn(book);
        
        // Mock repository save call
        when(bookRepository.save(book)).thenReturn(book);
        
        // Use lenient mocking for ModelMapper to handle dynamic arguments
        lenient().when(modelMapper.map(any(BookDTO.class), eq(Book.class))).thenReturn(book);
        lenient().when(modelMapper.map(any(Book.class), eq(BookDTO.class))).thenReturn(bookDTO);
        
        // Mock inventory repository call
        when(inventoryRepository.findByBookBookId(1L)).thenReturn(inventory);

        // Call the service method
        BookDTO updatedBook = bookService.updateBooks("Book Title", bookDTO);

        // Assertions
        assertNotNull(updatedBook);
        assertEquals(bookDTO, updatedBook);
    }


    @Test
    public void testUpdateBooksNotFound() {
        when(bookRepository.findByBookName("Nonexistent Title")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            bookService.updateBooks("Nonexistent Title", bookDTO);
        });
    }

    @Test
    public void testDeleteBook() {
        when(bookRepository.findByBookName("Book Title")).thenReturn(book);

        assertDoesNotThrow(() -> bookService.deleteBook("Book Title"));
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    public void testDeleteBookNotFound() {
        when(bookRepository.findByBookName("Nonexistent Title")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> bookService.deleteBook("Nonexistent Title"));
        verify(bookRepository, never()).delete(any(Book.class));
    }
}
