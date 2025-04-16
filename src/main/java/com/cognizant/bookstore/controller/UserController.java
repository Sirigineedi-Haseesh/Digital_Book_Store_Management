package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.bookstore.dto.UserLoginDTO;
import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;
import com.cognizant.bookstore.exceptions.InvalidCredentialsException;
import com.cognizant.bookstore.exceptions.UnauthorizedAccessException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public ResponseEntity<UserProfileDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
		return ResponseEntity.ok(userService.registerUser(dto));
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO dto) {
		try {
			return ResponseEntity.ok(userService.loginUser(dto));
		} catch (InvalidCredentialsException | UnauthorizedAccessException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	@GetMapping("/profile/{id}")
	public ResponseEntity<UserProfileDTO> getProfile(@PathVariable long id) {
		try {
			return ResponseEntity.ok(userService.getUserProfile(id));
		} catch (UserNotFoundException | UnauthorizedAccessException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<UserProfileDTO> updateProfile(@PathVariable long id, @Valid @RequestBody UserProfileDTO dto) {
		try {
			return ResponseEntity.ok(userService.updateUserProfile(id, dto));
		} catch (UserNotFoundException | UnauthorizedAccessException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@GetMapping("/allUsers")
	public ResponseEntity<List<UserProfileDTO>> getAllProfile() {
		List<UserProfileDTO> users = userService.getAllUser();
		if (!users.isEmpty()) {
			return ResponseEntity.ok(users);
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
	}

	@DeleteMapping("/deleteUser/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable long id) {
		try {
			return ResponseEntity.ok(userService.deleteUser(id));
		} catch (UserNotFoundException | UnauthorizedAccessException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
