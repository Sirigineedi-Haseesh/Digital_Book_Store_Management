package com.cognizant.bookstore.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
//@Data
@Getter
@Setter
@ToString
public class Book {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private long bookId;
    @Column(unique = true, nullable = false)
    private String isbn;
    @Column(unique = true, nullable = false)
    private String title;
    private String category;
    private long price;
    private String authorName;
    private String images;
    @OneToOne(mappedBy="book" ,cascade = CascadeType.ALL)
    @JsonIgnore
    private Inventory inventory;
}
