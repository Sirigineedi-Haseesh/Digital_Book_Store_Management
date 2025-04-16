package com.cognizant.bookstore.dto;

import com.cognizant.bookstore.model.Book;

import lombok.Data;


@Data
public class InventoryDTO {
	private long inventoryId;
	private Book book;
	private int stock;
}
