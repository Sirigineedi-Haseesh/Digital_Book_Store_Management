package com.cognizant.bookstore.exceptions;

public class BooksNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public BooksNotFoundException(String message) {
        super(message);
    }
}