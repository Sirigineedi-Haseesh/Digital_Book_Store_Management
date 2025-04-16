package com.cognizant.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String userName;

    @NotBlank(message = "Pas"
    		+ "sword cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name cannot be empty")
    private String fullName;

    @NotBlank(message = "Address cannot be empty")
    private String address;
    
//    private String role;
}


//
//
//dev - developer 
//
//UAT - user acceptance testing
//SIT - use environment to test the application
//perf region(single application runs in multiple server)--profile in springBoot / performance testing) - used for testing the time taking to perform the operation