package com.cognizant.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.bookstore.dto.AuthenticationRequestDTO;
import com.cognizant.bookstore.dto.AuthenticationResponseDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;
import com.cognizant.bookstore.exceptions.UserAlreadyExistException;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.service.AuthenticationService;
import com.cognizant.bookstore.service.UserServiceImpl;
import com.cognizant.bookstore.util.JwtUtil;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	UserServiceImpl userDetailsService;

	@Autowired
	AuthenticationService authService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	

	@PostMapping("/saveUser")
	public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("Received request to register a new user with username: {}", userRegisterDTO.getUsername());

        try {
            // Register the user
            User savedUser = authService.register(userRegisterDTO);
            log.info("Successfully registered user with ID: {}", savedUser.getUserId());
            return ResponseEntity.ok(savedUser);

        } catch (UserAlreadyExistException e) {
            log.warn("Registration failed for username: {}. Reason: {}", userRegisterDTO.getUsername(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error occurred during registration for username: {}", userRegisterDTO.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthenticationRequestDTO request) {
        log.info("Received login request for username: {}", request.getUsername());

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);

            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername(), role);
            log.info("Successfully authenticated user: {} with role: {}", request.getUsername(), role);
            return ResponseEntity.ok(new AuthenticationResponseDTO(token));

        } catch (BadCredentialsException ex) {
            log.warn("Invalid credentials for username: {}", request.getUsername(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");

        } catch (AuthenticationException ex) {
            log.error("Authentication failed for username: {}. Reason: {}", request.getUsername(), ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + ex.getMessage());
        }
    }

}