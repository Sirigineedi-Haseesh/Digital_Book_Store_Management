package com.cognizant.bookstore.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.dto.UserLoginDTO;
import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserProfileDTO registerUser(UserRegisterDTO dto) {
        User user = modelMapper.map(dto, User.class);
        user.setRole("USER"); // Setting the default role
        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserProfileDTO.class);
    }

    @Override
    public String loginUser(UserLoginDTO dto) {
        Optional<User> userOpt = userRepository.findByUserName(dto.getUserName());
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(dto.getPassword())) {
            return "Login successful"; // Add JWT token generation here
        }
        return "Invalid credentials";
    }

    @Override
    public UserProfileDTO getUserProfile(long userId) {
    	Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            // Return a meaningful response or throw a more specific exception
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        return modelMapper.map(userOptional.get(), UserProfileDTO.class);
    }

    @Override
    public UserProfileDTO updateUserProfile(long userId, UserProfileDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        modelMapper.map(dto, user); // Ensure correct mapping to update the entity
        User updated = userRepository.save(user);
        return modelMapper.map(updated, UserProfileDTO.class); // Convert updated entity back to DTO
    }


	@Override
	public List<UserProfileDTO> getAllUser() {
		List<User> allUsers = userRepository.findAll();
		return allUsers.stream()
	            .map(user -> modelMapper.map(user, UserProfileDTO.class))
	            .toList();
		
	}

	@Override
	public String deleteUser(long userId) {
		Optional<User> user = userRepository.findById(userId);
//		User user = modelMapper.map(userprofile.get(), User.class);
	    userRepository.delete(user.get());
	    return "User with username " + user.get().getUserName() + " has been deleted successfully.";
	}
    
}
