package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cognizant.bookstore.dto.UserLoginDTO;
import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;
import com.cognizant.bookstore.exceptions.InvalidCredentialsException;
import com.cognizant.bookstore.exceptions.UnauthorizedAccessException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    // Register a new user
//    @PostMapping("/register")
//    public ResponseEntity<UserProfileDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(dto));
//    }
//
//    // Login user and generate JWT token
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO dto) {
//        try {
//            return ResponseEntity.ok(userService.loginUser(dto));
//        } catch (InvalidCredentialsException | UnauthorizedAccessException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
//    }

    // Fetch user profile by username
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.getUserProfileByUsername(username));
        } catch (UserNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update user profile by username
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/update/{username}")
    public ResponseEntity<UserProfileDTO> updateProfile(@PathVariable String username, @Valid @RequestBody UserProfileDTO dto) {
        try {
            return ResponseEntity.ok(userService.updateUserProfileByUsername(username, dto));
        } catch (UserNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Fetch all user profiles (Admin only)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/allUsers")
    public ResponseEntity<List<UserProfileDTO>> getAllProfile() {
        List<UserProfileDTO> users = userService.getAllUser();
        return users.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).build() : ResponseEntity.ok(users);
    }

    // Delete user by username (Admin only)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteUser/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.deleteUserByUsername(username));
        } catch (UserNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
