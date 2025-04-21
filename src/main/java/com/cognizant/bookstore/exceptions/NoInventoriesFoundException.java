package com.cognizant.bookstore.exceptions;

public class NoInventoriesFoundException extends RuntimeException{
	private static final long serialVersionUID = 1L;

    public NoInventoriesFoundException(String message) {
        super(message);
    }
}
