package com.cognizant.bookstore.exceptions;

public class DuplicateTitleException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateTitleException(String message) {
        super(message);
    }
}