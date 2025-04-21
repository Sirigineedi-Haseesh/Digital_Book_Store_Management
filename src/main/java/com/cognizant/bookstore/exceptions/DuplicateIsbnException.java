package com.cognizant.bookstore.exceptions;

public class DuplicateIsbnException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateIsbnException(String message) {
        super(message);
    }
}