package com.cognizant.bookstore.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.dto.UserLoginDTO;
import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;
import com.cognizant.bookstore.exceptions.InvalidCredentialsException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.UserRepository;
import com.cognizant.bookstore.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a new user
    @Override
    public UserProfileDTO registerUser(UserRegisterDTO dto) {
        log.info("Registering user with username: {}", dto.getUsername());
        User user = modelMapper.map(dto, User.class);
        user.setRole("USER"); // Default role assignment
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Secure password storage
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getUserId());
        return modelMapper.map(savedUser, UserProfileDTO.class);
    }

    // Login a user and generate JWT token
    @Override
    public String loginUser(UserLoginDTO dto) {
        log.info("Attempting login for username: {}", dto.getUsername());
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));

        // Validate password using password encoder
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for username: {}", dto.getUsername());
            throw new InvalidCredentialsException("Invalid credentials for username: " + dto.getUsername());
        }

        log.info("Login successful for username: {}", dto.getUsername());
        return jwtUtil.generateToken(user.getUsername(), user.getRole()); // Returns JWT token
    }

    // Fetch user profile by ID
    @Override
    public UserProfileDTO getUserProfileByUsername(String username) {
        log.info("Fetching profile for username: {}", username);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
        
        log.info("Profile fetched successfully for username: {}", username);
        return modelMapper.map(user, UserProfileDTO.class);
    }

    @Override
    public UserProfileDTO updateUserProfileByUsername(String username, UserProfileDTO dto) {
        log.info("Updating profile for username: {}", username);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
        
        modelMapper.map(dto, user);
        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for username: {}", username);
        return modelMapper.map(updatedUser, UserProfileDTO.class);
    }


    // Get all users
    @Override
    public List<UserProfileDTO> getAllUser() {
        log.info("Fetching all users");
        List<User> allUsers = userRepository.findAll();
        log.info("Fetched {} users", allUsers.size());
        return allUsers.stream()
                .map(user -> modelMapper.map(user, UserProfileDTO.class))
                .collect(Collectors.toList());
    }

    // Delete user by ID
    @Override
    public String deleteUserByUsername(String username) {
        log.info("Deleting user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));

        userRepository.delete(user);
        log.info("User with username {} deleted successfully", username);
        return "User with username " + username + " has been deleted successfully.";
    }


    // Load user details for authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }
}