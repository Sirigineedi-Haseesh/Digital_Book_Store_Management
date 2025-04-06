package com.cognizant.bookstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Inventory {
	@Id
	private long inventoryId;
	@OneToOne
	private Book book;
	private int stock;
}
