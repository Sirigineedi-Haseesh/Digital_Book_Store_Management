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

import lombok.extern.slf4j.Slf4j;
 
@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {
 
    @Autowired
    private UserService userService;
 
    
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<String> adminDashboard() {
        log.info("Received request to access Admin Dashboard");
        try {
            log.info("Admin Dashboard access granted");
            return ResponseEntity.ok("Welcome to Admin Dashboard!");
        } catch (Exception e) {
            log.error("Unexpected error occurred while accessing Admin Dashboard. Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/reports")
    public ResponseEntity<String> viewReports() {
        log.info("Received request to view Admin Reports");
        try {
            log.info("Admin Reports access granted");
            return ResponseEntity.ok("Admin Reports Access Granted!");
        } catch (Exception e) {
            log.error("Unexpected error occurred while accessing Admin Reports. Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/allUsers")
    public ResponseEntity<?> getAllProfile() {
        log.info("Received request to fetch all user profiles");
        try {
            List<UserProfileDTO> users = userService.getAllUser();
            if (users.isEmpty()) {
                log.info("No users found in the system");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users found");
            }
            log.info("Successfully fetched {} user profile(s)", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching all user profiles. Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteUser/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        log.info("Received request to delete user with username: {}", username);
        try {
            String result = userService.deleteUserByUsername(username);
            log.info("Successfully deleted user with username: {}", username);
            return ResponseEntity.ok(result);
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            log.warn("Unauthorized access attempt for username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting user with username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/update/{username}")
    public ResponseEntity<?> updateProfile(@PathVariable String username, @RequestBody UserProfileDTO dto) {
        log.info("Received request to update profile for username: {}", username);
        try {
            UserProfileDTO updatedProfile = userService.updateUserProfileByUsername(username, dto);
            log.info("Successfully updated profile for username: {}", username);
            return ResponseEntity.ok(updatedProfile);
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            log.warn("Unauthorized access attempt to update username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating profile for username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/assignRole/{username}/{role}")
    public ResponseEntity<String> assignRoleToUser(@PathVariable String username, @PathVariable String role) {
        log.info("Received request to assign role: {} to user with username: {}", role, username);
        try {
            String result = userService.assignRoleByUsername(username, role);
            log.info("Successfully assigned role: {} to user with username: {}", role, username);
            return ResponseEntity.ok(result);
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            log.warn("Unauthorized access attempt to assign role for username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid role provided for username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while assigning role to username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}