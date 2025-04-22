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
import com.cognizant.bookstore.exceptions.UserAlreadyExistException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.UserRepository;
import com.cognizant.bookstore.util.JwtUtil;
 
import lombok.extern.slf4j.Slf4j;
 
@Service
public class UserServiceImpl implements UserService,UserDetailsService{
 
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private ModelMapper modelMapper;
 
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    @Autowired
    private JwtUtil jwtUtil;
 
    // Register a new user
  
 
    // Login a user and generate JWT token
    @Override
    public String loginUser(UserLoginDTO dto) {
        
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));
 
        // Validate password using password encoder
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            
            throw new InvalidCredentialsException("Invalid credentials for username: " + dto.getUsername());
        }
 
        
        return jwtUtil.generateToken(user.getUsername(), user.getRole()); // Returns JWT token
    }
 
    // Fetch user profile by ID
    @Override
    public UserProfileDTO getUserProfileByUsername(String username) {
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
        
        return modelMapper.map(user, UserProfileDTO.class);
    }
 
    @Override
    public UserProfileDTO updateUserProfileByUsername(String username, UserProfileDTO dto){
        
 
        // Fetch the existing user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
        // Check if DTO is null
        if (dto != null) {

            // Update fields explicitly to avoid modifying userId
            if (dto.getUsername() != null ) user.setUsername(dto.getUsername());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            if (dto.getFullName() != null) user.setFullName(dto.getFullName());
            if (dto.getAddress() != null) user.setAddress(dto.getAddress());
            if (dto.getRole() != null) user.setRole(dto.getRole());
        } else {
            
        }
 
        // Save the updated user
        User updatedUser = userRepository.save(user);
        // Map the updated user entity back to the DTO
        return modelMapper.map(updatedUser, UserProfileDTO.class);
    }
 
 
 
    // Get all users
    @Override
    public List<UserProfileDTO> getAllUser() {
        
        List<User> allUsers = userRepository.findAll();
        
        return allUsers.stream()
                .map(user -> modelMapper.map(user, UserProfileDTO.class))
                .collect(Collectors.toList());
    }
 
    // Delete user by ID
    @Override
    public String deleteUserByUsername(String username) {
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
 
        userRepository.delete(user);
        
        return "User with username " + username + " has been deleted successfully.";
    }
 
 
    // Load user details for authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
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