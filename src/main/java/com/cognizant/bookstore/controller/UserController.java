package com.cognizant.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.cognizant.bookstore.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
 
    @Autowired
    private UserService userService;
 
    @PostMapping("/register")
    public ResponseEntity<UserProfileDTO> register(@RequestBody UserRegisterDTO dto) {
        return ResponseEntity.ok(userService.registerUser(dto));
    }
 
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO dto) {
        return ResponseEntity.ok(userService.loginUser(dto));
    }
 
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }
 
    @PutMapping("/update/{id}")
    public ResponseEntity<UserProfileDTO> updateProfile(@PathVariable long id, @RequestBody UserProfileDTO dto) {
        return ResponseEntity.ok(userService.updateUserProfile(id, dto));
    }
    @GetMapping("/allUsers")
    public ResponseEntity<List<UserProfileDTO>> getAllProfile(){
    	return ResponseEntity.ok(userService.getAllUser());
    }
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id){
    	return ResponseEntity.ok(userService.deleteUser(id));
    }
}