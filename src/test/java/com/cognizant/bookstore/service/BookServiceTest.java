package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        // Mock existing book entity
        Book existingBook = new Book();
        existingBook.setBookId(1L);
        existingBook.setTitle("A");
        existingBook.setCategory("Fiction");
        existingBook.setPrice(500);
        existingBook.setAuthorName("Old Author");
        existingBook.setIsbn("1234567890");

        // Mock existing inventory entity
        Inventory existingInventory = new Inventory();
        existingInventory.setInventoryId(1L);
        existingInventory.setStock(50);
        existingInventory.setBook(existingBook);

        existingBook.setInventory(existingInventory);

        // Mock updated BookDTO
        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setBookId(1L);
        updatedBookDTO.setTitle("B");
        updatedBookDTO.setCategory("Sci-Fi");
        updatedBookDTO.setPrice(700);
        updatedBookDTO.setAuthorName("New Author");
        updatedBookDTO.setIsbn("0987654321");
        updatedBookDTO.setInventory(existingInventory);

        // Mocking repository and mapper behavior
        lenient().when(bookRepository.findByBookName("A")).thenReturn(existingBook);
        lenient().when(modelMapper.map(any(BookDTO.class), eq(Book.class))).thenReturn(existingBook);
        lenient().when(bookRepository.save(existingBook)).thenReturn(existingBook);
        lenient().when(modelMapper.map(any(Book.class), eq(BookDTO.class))).thenReturn(updatedBookDTO);

        // Call the service method
        BookDTO result = bookService.updateBooks("A", updatedBookDTO);

        // Assertions
        assertNotNull(result);
        assertEquals("B", result.getTitle());
        assertEquals("Sci-Fi", result.getCategory());
        assertEquals(700, result.getPrice());
        assertEquals("New Author", result.getAuthorName());
        assertEquals("0987654321", result.getIsbn());
        assertNotNull(result.getInventory());
        assertEquals(existingInventory.getStock(), result.getInventory().getStock());

        // Verify interactions
        verify(bookRepository, times(1)).findByBookName("A");
        verify(bookRepository, times(1)).save(existingBook);
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
