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
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.service.AuthenticationService;
import com.cognizant.bookstore.service.UserServiceImpl;
import com.cognizant.bookstore.util.JwtUtil;

@RestController

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
	public User register(@RequestBody User user) {
		return authService.save(user);
	}

	@PostMapping("/login")

	public ResponseEntity<?> login(@RequestBody AuthenticationRequestDTO request) {

		try {
			Authentication authentication = authenticationManager.authenticate(

					new UsernamePasswordAuthenticationToken

					(request.getUsername(), request.getPassword()));
			
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();

			String role = userDetails.getAuthorities().stream().findFirst().
					map(GrantedAuthority::getAuthority)
					.orElse(null);
			String token = jwtUtil.generateToken(userDetails.getUsername(), role);
			return ResponseEntity.ok(new AuthenticationResponseDTO(token));
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
					body("Invalid username or password");
		} catch (AuthenticationException ex) {
			return ResponseEntity.
					status(HttpStatus.UNAUTHORIZED).body("Authentication failed " + ex.getMessage());
		}

	}

}