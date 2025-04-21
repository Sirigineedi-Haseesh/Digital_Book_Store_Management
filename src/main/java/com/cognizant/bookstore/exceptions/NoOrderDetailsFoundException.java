package com.cognizant.bookstore.exceptions;

public class NoOrderDetailsFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public NoOrderDetailsFoundException(String message) {
        super(message);
    }
}
