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
 
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.getUserProfileByUsername(username));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error");
        }
    }
 
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/update/{username}")
    public ResponseEntity<?> updateProfile(@PathVariable String username, @RequestBody UserProfileDTO dto) {
        try {
            return ResponseEntity.ok(userService.updateUserProfileByUsername(username, dto));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error");
        }
    }
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/changePassword/{username}")
    public ResponseEntity<?> changePassword(@PathVariable String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        try {
            return ResponseEntity.ok(userService.changePassword(username, oldPassword, newPassword));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error");
        }
    }
 
}