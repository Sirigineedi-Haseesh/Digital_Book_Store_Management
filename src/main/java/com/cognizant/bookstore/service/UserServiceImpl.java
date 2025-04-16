package com.cognizant.bookstore.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.dto.UserLoginDTO;
import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;
import com.cognizant.bookstore.exceptions.InvalidCredentialsException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserProfileDTO registerUser(UserRegisterDTO dto) {
        log.info("Registering user with username: {}", dto.getUserName());
        User user = modelMapper.map(dto, User.class);
        user.setRole("USER"); // Setting the default role
        User saved = userRepository.save(user);
        log.info("User registered successfully with ID: {}", saved.getUserId());
        return modelMapper.map(saved, UserProfileDTO.class);
    }

    @Override
    public String loginUser(UserLoginDTO dto) {
        log.info("Attempting login for username: {}", dto.getUserName());
        Optional<User> userOpt = userRepository.findByUserName(dto.getUserName());
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(dto.getPassword())) {
            log.info("Login successful for username: {}", dto.getUserName());
            return "Login successful"; // Add JWT token generation here
        }
        log.warn("Invalid login attempt for username: {}", dto.getUserName());
        throw new InvalidCredentialsException("Invalid credentials for username: " + dto.getUserName());
    }

    @Override
    public UserProfileDTO getUserProfile(long userId) {
        log.info("Fetching profile for user ID: {}", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("User with ID {} not found", userId);
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        log.info("Profile fetched successfully for user ID: {}", userId);
        return modelMapper.map(userOptional.get(), UserProfileDTO.class);
    }

    @Override
    public UserProfileDTO updateUserProfile(long userId, UserProfileDTO dto) {
        log.info("Updating profile for user ID: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        modelMapper.map(dto, user); // Update the entity using ModelMapper
        User updated = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);
        return modelMapper.map(updated, UserProfileDTO.class);
    }

    @Override
    public List<UserProfileDTO> getAllUser() {
        log.info("Fetching all users");
        List<User> allUsers = userRepository.findAll();
        log.info("Fetched {} users", allUsers.size());
        return allUsers.stream()
            .map(user -> modelMapper.map(user, UserProfileDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public String deleteUser(long userId) {
        log.info("Deleting user with ID: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            log.info("User with ID {} deleted successfully", userId);
            return "User with username " + user.get().getUserName() + " has been deleted successfully.";
        } else {
            log.error("User with ID {} not found", userId);
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
    }
}
