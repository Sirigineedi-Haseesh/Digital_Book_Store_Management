package com.cognizant.bookstore.service;
 
import java.util.List;
 
import org.springframework.security.core.userdetails.UserDetails;
 
import com.cognizant.bookstore.dto.UserLoginDTO;
import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;
 
public interface UserService {
    UserProfileDTO registerUser(UserRegisterDTO dto);
    String loginUser(UserLoginDTO dto);
    UserProfileDTO getUserProfileByUsername(String username); // ✅ Changed userId to username
    UserProfileDTO updateUserProfileByUsername(String username, UserProfileDTO dto); // ✅ Changed userId to username
    List<UserProfileDTO> getAllUser();
    String deleteUserByUsername(String username); // ✅ Changed userId to username
    UserDetails loadUserByUsername(String username);
    String assignRoleByUsername(String username, String role);
    String changePassword(String username, String oldPassword, String newPassword);
}