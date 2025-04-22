package com.cognizant.bookstore.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cognizant.bookstore.repository.UserRepository;
import com.cognizant.bookstore.dto.UserRegisterDTO;
import com.cognizant.bookstore.exceptions.UserAlreadyExistException;
import com.cognizant.bookstore.model.User;

@Service
public class AuthenticationService {
	@Autowired
	UserRepository userRepo;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	private ModelMapper modelMapper;
	
	public User register(UserRegisterDTO userRegisterDTO) throws UserAlreadyExistException{
        // Check if a user with the same username already exists
        if (userRepo.findByUsername(userRegisterDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("A user with the same username already exists");
        }
 
        // Convert DTO to Entity
        User user = modelMapper.map(userRegisterDTO, User.class);
        // Encode the password
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
 
        // Save the user in the database
        User savedUser = userRepo.save(user);
 
        // Optionally, you can convert the saved entity back into a DTO if required
        // UserRegisterDTO savedUserDTO = modelMapper.map(savedUser, UserRegisterDTO.class);
 
        return savedUser;
    }

}
