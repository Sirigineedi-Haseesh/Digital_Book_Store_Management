package com.cognizant.bookstore.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cognizant.bookstore.dto.BookDTO;
import com.cognizant.bookstore.exceptions.*;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.Inventory;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookService bookService;

    private BookDTO bookDTO;
    private Book book;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize objects for reuse
        bookDTO = new BookDTO();
        bookDTO.setTitle("Sample Book");
        bookDTO.setIsbn("123456789");
        bookDTO.setCategory("Fiction");
        bookDTO.setPrice(500);
        bookDTO.setAuthorName("John Doe");

        book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());

        inventory = new Inventory();
        book.setInventory(inventory);
    }

    // Test for saveBook
    @Test
    void testSaveBook_Success() {
        when(bookRepository.existsByTitle(bookDTO.getTitle())).thenReturn(false);
        when(bookRepository.existsByIsbn(bookDTO.getIsbn())).thenReturn(false);
        when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.saveBook(bookDTO);

        assertNotNull(result);
        assertEquals("Sample Book", result.getTitle());
        verify(bookRepository, times(1)).save(book);
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void testSaveBook_DuplicateTitle() {
        when(bookRepository.existsByTitle(bookDTO.getTitle())).thenReturn(true);

        DuplicateTitleException exception = assertThrows(DuplicateTitleException.class, 
            () -> bookService.saveBook(bookDTO));

        assertEquals("Book with title 'Sample Book' already exists", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testSaveBook_DuplicateIsbn() {
        when(bookRepository.existsByIsbn(bookDTO.getIsbn())).thenReturn(true);

        DuplicateIsbnException exception = assertThrows(DuplicateIsbnException.class, 
            () -> bookService.saveBook(bookDTO));

        assertEquals("Book with ISBN '123456789' already exists ISBN Must Be Unique", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    // Test for getBooks
    @Test
    void testGetBooks_Success() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        List<BookDTO> result = bookService.getBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBooks_NoBooksFound() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        BooksNotFoundException exception = assertThrows(BooksNotFoundException.class, 
            () -> bookService.getBooks());

        assertEquals("No books found", exception.getMessage());
    }

    // Test for updateBooks
    @Test
    void testUpdateBooks_Success() {
        when(bookRepository.findByBookName("Sample Book")).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.updateBooks("Sample Book", bookDTO);

        assertNotNull(result);
        assertEquals("Sample Book", result.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testUpdateBooks_BookNotFound() {
        when(bookRepository.findByBookName("Nonexistent Book")).thenReturn(null);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, 
            () -> bookService.updateBooks("Nonexistent Book", bookDTO));

        assertEquals("Book not found with title: Nonexistent Book", exception.getMessage());
    }

    // Test for updateBooksPatch
    @Test
    void testUpdateBooksPatch_Success() {
        when(bookRepository.findByBookName("Sample Book")).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.updateBooksPatch("Sample Book", bookDTO);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testUpdateBooksPatch_BookNotFound() {
        when(bookRepository.findByBookName("Nonexistent Book")).thenReturn(null);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, 
            () -> bookService.updateBooksPatch("Nonexistent Book", bookDTO));

        assertEquals("Book not found with title: Nonexistent Book", exception.getMessage());
    }

    // Test for deleteBook
    @Test
    void testDeleteBook_Success() {
        when(bookRepository.findByBookName("Sample Book")).thenReturn(book);

        bookService.deleteBook("Sample Book");

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void testDeleteBook_BookNotFound() {
        when(bookRepository.findByBookName("Nonexistent Book")).thenReturn(null);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, 
            () -> bookService.deleteBook("Nonexistent Book"));

        assertEquals("Book not found with title: Nonexistent Book", exception.getMessage());
    }
}
