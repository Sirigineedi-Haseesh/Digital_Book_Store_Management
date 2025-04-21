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
        try {
            return ResponseEntity.ok("Welcome to Admin Dashboard!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
 
    // ✅ View system reports
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/reports")
    public ResponseEntity<String> viewReports() {
        try {
            return ResponseEntity.ok("Admin Reports Access Granted!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
 
    // ✅ Get all users in the system
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/allUsers")
    public ResponseEntity<?> getAllProfile() {
        try {
            List<UserProfileDTO> users = userService.getAllUser();
            return users.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users found") : ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
 
    // ✅ Delete a user by username
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteUser/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.deleteUserByUsername(username));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
 
    // ✅ Update a user's profile
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/update/{username}")
    public ResponseEntity<?> updateProfile(@PathVariable String username, @RequestBody UserProfileDTO dto) {
        try {
            UserProfileDTO updatedProfile = userService.updateUserProfileByUsername(username, dto);
            return ResponseEntity.ok(updatedProfile);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
 
    // ✅ Assign Role to a User
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/assignRole/{username}/{role}")
    public ResponseEntity<String> assignRoleToUser(@PathVariable String username, @PathVariable String role) {
        try {
            String result = userService.assignRoleByUsername(username, role);
            return ResponseEntity.ok(result);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}