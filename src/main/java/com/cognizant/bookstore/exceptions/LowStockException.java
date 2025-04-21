package com.cognizant.bookstore.exceptions;

public class LowStockException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LowStockException(String message) {
        super(message);
    }
}