package com.cognizant.bookstore.exceptions;

public class InventoryException extends RuntimeException {
	    private String message;

	    public InventoryException(String message) {
	        super(message); // Call the constructor of RuntimeException
	        this.message = message;
	}
}