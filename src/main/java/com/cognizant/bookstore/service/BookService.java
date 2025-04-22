package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.BookDTO;

import java.util.List;

public interface BookService {

    BookDTO saveBook(BookDTO bookDTO);

    List<BookDTO> getBooks();

    BookDTO updateBooks(String title, BookDTO bookDTO);

    BookDTO updateBooksPatch(String title, BookDTO bookDTO);
    
    void deleteBook(String title);
}
