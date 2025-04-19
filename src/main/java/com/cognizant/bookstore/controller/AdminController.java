package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.exceptions.UnauthorizedAccessException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // ✅ Admin Dashboard
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Welcome to Admin Dashboard!");
    }

    // ✅ View system reports
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/reports")
    public ResponseEntity<String> viewReports() {
        return ResponseEntity.ok("Admin Reports Access Granted!");
    }

    // ✅ Get all users in the system
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/allUsers")
    public ResponseEntity<List<UserProfileDTO>> getAllProfile() {
        List<UserProfileDTO> users = userService.getAllUser();
        return users.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).build() : ResponseEntity.ok(users);
    }

    // ✅ Delete a user by username (Updated from `userId` to `username`)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteUser/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.deleteUserByUsername(username)); // ✅ Changed to username-based deletion
        } catch (UserNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
