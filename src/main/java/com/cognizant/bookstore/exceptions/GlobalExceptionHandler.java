package com.cognizant.bookstore.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<String> handleBookNotFoundException(BookNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(OutOfStockException.class)
	public ResponseEntity<String> handleOutOfStockException(OutOfStockException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(InvalidOrderException.class)
	public ResponseEntity<String> handleInvalidOrderException(InvalidOrderException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(InventoryNotFoundException.class)
	public ResponseEntity<String> handleInventoryNotFoundException(InventoryNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<String> handleInsufficientStockException(InsufficientStockException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(PaymentFailedException.class)
	public ResponseEntity<String> handlePaymentFailedException(PaymentFailedException ex) {
		return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(ex.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<String> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}
}
