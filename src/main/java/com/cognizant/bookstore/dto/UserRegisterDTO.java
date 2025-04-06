package com.cognizant.bookstore.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
	private String userName;
	private String password;
	private String email;
	private String fullName;
	private String address;
}
