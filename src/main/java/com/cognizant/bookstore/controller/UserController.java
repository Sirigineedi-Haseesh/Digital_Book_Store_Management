package com.cognizant.bookstore.controller; 
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
import lombok.extern.slf4j.Slf4j;
 
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
 
    @Autowired
    private UserService userService;
 
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        log.info("Received request to fetch profile for username: {}", username);
        try {
            UserProfileDTO profile = userService.getUserProfileByUsername(username);
            log.info("Successfully fetched profile for username: {}", username);
            return ResponseEntity.ok(profile);
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            log.warn("Unauthorized access for username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching profile for username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error");
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
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
            log.warn("Unauthorized access for username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating profile for username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error");
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/changePassword/{username}")
    public ResponseEntity<?> changePassword(@PathVariable String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        log.info("Received request to change password for username: {}", username);
        try {
            String message = userService.changePassword(username, oldPassword, newPassword);
            log.info("Password changed successfully for username: {}", username);
            return ResponseEntity.ok(message);
        } catch (InvalidCredentialsException e) {
            log.warn("Invalid credentials for username: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}. Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while changing password for username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error");
        }
    }

 
}