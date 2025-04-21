package com.cognizant.bookstore.service;
 
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
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
 
//    @Override
//    public UserProfileDTO updateUserProfileByUsername(String username, UserProfileDTO dto) {
//        log.info("Updating profile for username: {}", username);
//        User user = userRepository.findByUsername(username)
//            .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
//        
//        modelMapper.map(dto, user);
//        User updatedUser = userRepository.save(user);
//        log.info("Profile updated successfully for username: {}", username);
//        return modelMapper.map(updatedUser, UserProfileDTO.class);
//    }
    @Override
    public UserProfileDTO updateUserProfileByUsername(String username, UserProfileDTO dto) {
        log.info("Updating profile for username: {}", username);
 
        // Fetch the existing user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
 
        // Check if DTO is null
        if (dto != null) {
            log.info("DTO is not null, updating fields...");
 
            // Update fields explicitly to avoid modifying userId
            if (dto.getUsername() != null) user.setUsername(dto.getUsername());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            if (dto.getFullName() != null) user.setFullName(dto.getFullName());
            if (dto.getAddress() != null) user.setAddress(dto.getAddress());
            if (dto.getRole() != null) user.setRole(dto.getRole());
        } else {
            log.warn("DTO is null, no updates performed.");
        }
 
        // Save the updated user
        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for username: {}", username);
 
        // Map the updated user entity back to the DTO
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
    public String assignRoleByUsername(String username, String role) throws UserNotFoundException, IllegalArgumentException {
        // Fetch the user by username
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("User with username " + username + " not found.");
        }
 
        // Validate the role (assuming a predefined list of roles)
        List<String> validRoles = Arrays.asList("ADMIN", "USER");
        if (!validRoles.contains(role)) {
            throw new IllegalArgumentException("Invalid role: " + role + ". Valid roles are " + validRoles);
        }
 
        // Assign role to the user
        User user = userOptional.get();
        user.setRole(role);
 
        // Save the updated user back to the repository
        userRepository.save(user);
 
        // Return confirmation message
        return "Role " + role + " has been successfully assigned to user " + username;
    }
    @Override
    public String changePassword(String username, String oldPassword, String newPassword) throws UserNotFoundException, InvalidCredentialsException {
        // Fetch the user by username
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("User with username " + username + " not found.");
        }
 
        User user = userOptional.get();
 
        // Validate old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect.");
        }
 
        // Encrypt and set the new password
        user.setPassword(passwordEncoder.encode(newPassword));
 
        // Save the updated user back to the repository
        userRepository.save(user);
 
        // Return confirmation message
        return "Password has been successfully updated for user " + username;
    }
 
}