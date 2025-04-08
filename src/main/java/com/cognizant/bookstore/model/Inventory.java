package com.cognizant.bookstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Inventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long inventoryId;
	@OneToOne
	@JsonIgnore
	private Book book;
	private int stock;
}
