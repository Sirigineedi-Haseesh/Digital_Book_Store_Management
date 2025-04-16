package com.cognizant.bookstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
//@Data
@Getter
@Setter
@ToString
public class Inventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long inventoryId;
	@OneToOne
	@JoinColumn(name = "bookId")
	
	@JsonIgnore
	@ToString.Exclude //Added new
	private Book book;
	private int stock;
}