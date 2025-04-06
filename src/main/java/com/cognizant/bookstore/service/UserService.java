package com.cognizant.bookstore.service;
import java.util.List;

import com.cognizant.bookstore.dto.UserLoginDTO;
import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;

public interface UserService {
    UserProfileDTO registerUser(UserRegisterDTO dto);
    String loginUser(UserLoginDTO dto);
    UserProfileDTO getUserProfile(long userId);
    UserProfileDTO updateUserProfile(long userId, UserProfileDTO dto);
    List<UserProfileDTO> getAllUser();
    String deleteUser(long userId);
}