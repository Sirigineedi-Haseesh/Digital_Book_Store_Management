package com.cognizant.bookstore.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
	private long userId;
	private String userName;
	private String email;
	private String fullName;
	private String address;
	private String role;
}
