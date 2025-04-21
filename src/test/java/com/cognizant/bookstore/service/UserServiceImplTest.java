package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cognizant.bookstore.dto.UserLoginDTO;
import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;
import com.cognizant.bookstore.exceptions.InvalidCredentialsException;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.UserRepository;
import com.cognizant.bookstore.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserProfileDTO userProfileDTO;
    private UserRegisterDTO userRegisterDTO;
    private UserLoginDTO userLoginDTO;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("john_doe");
        user.setPassword("encoded_password");
        user.setEmail("john@example.com");
        user.setRole("USER");

        userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUsername("john_doe");
        userProfileDTO.setEmail("john@example.com");

        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUsername("john_doe");
        userRegisterDTO.setPassword("password123");
        userRegisterDTO.setEmail("john@example.com");

        userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("john_doe");
        userLoginDTO.setPassword("password123");
    }

    // Test for registering a user
    @Test
    void testRegisterUser() {
        when(modelMapper.map(userRegisterDTO, User.class)).thenReturn(user);
        when(passwordEncoder.encode(userRegisterDTO.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserProfileDTO.class)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.registerUser(userRegisterDTO);

        assertNotNull(result);
        assertEquals(userProfileDTO.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test for login with correct credentials
    @Test
    void testLoginUserSuccess() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getUsername(), user.getRole())).thenReturn("jwt_token");

        String token = userService.loginUser(userLoginDTO);

        assertEquals("jwt_token", token);
        verify(userRepository, times(1)).findByUsername("john_doe");
    }

    // Test for login with invalid credentials
    @Test
    void testLoginUserInvalidCredentials() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(userLoginDTO));
        verify(userRepository, times(1)).findByUsername("john_doe");
    }

    // Test for fetching user profile by username
    @Test
    void testGetUserProfileByUsername() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserProfileDTO.class)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.getUserProfileByUsername("john_doe");

        assertNotNull(result);
        assertEquals(userProfileDTO.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByUsername("john_doe");
    }

    // Test for fetching user profile by username (UserNotFoundException)
    @Test
    void testGetUserProfileByUsernameNotFound() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserProfileByUsername("unknown_user"));
        verify(userRepository, times(1)).findByUsername("unknown_user");
    }

    // Test for updating user profile
    @Test
    void testUpdateUserProfileByUsername() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserProfileDTO.class)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.updateUserProfileByUsername("john_doe", userProfileDTO);

        assertNotNull(result);
        assertEquals(userProfileDTO.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test for updating user profile (UserNotFoundException)
    @Test
    void testUpdateUserProfileByUsernameNotFound() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserProfileByUsername("unknown_user", userProfileDTO));
        verify(userRepository, times(1)).findByUsername("unknown_user");
    }

    // Test for deleting user by username
    @Test
    void testDeleteUserByUsername() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        String result = userService.deleteUserByUsername("john_doe");

        assertEquals("User with username john_doe has been deleted successfully.", result);
        verify(userRepository, times(1)).delete(user);
    }

    // Test for deleting user by username (UserNotFoundException)
    @Test
    void testDeleteUserByUsernameNotFound() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserByUsername("unknown_user"));
        verify(userRepository, times(1)).findByUsername("unknown_user");
    }
    @Test
    void testAssignRoleSuccess() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        String result = userService.assignRoleByUsername("john_doe", "ADMIN");

        assertEquals("Role ADMIN has been successfully assigned to user john_doe", result);
        assertEquals("ADMIN", user.getRole());
        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(userRepository, times(1)).save(user);
    }

    // Test for assigning a role to a non-existing user
    @Test
    void testAssignRoleUserNotFound() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.assignRoleByUsername("unknown_user", "ADMIN"));
        verify(userRepository, times(1)).findByUsername("unknown_user");
        verify(userRepository, never()).save(any(User.class));
    }

    // Test for assigning an invalid role
    @Test
    void testAssignRoleInvalidRole() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        List<String> validRoles = Arrays.asList("ADMIN", "USER");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.assignRoleByUsername("john_doe", "INVALID_ROLE"));
        assertEquals("Invalid role: INVALID_ROLE. Valid roles are " + validRoles, exception.getMessage());
        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void testChangePasswordSuccess() {
        // Mock the user repository to return the user
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        // Mock the password encoder to match the old password
        when(passwordEncoder.matches("old_password", "encoded_password")).thenReturn(true);
        // Mock the password encoding for the new password
        when(passwordEncoder.encode("new_password")).thenReturn("encoded_new_password");

        // Call the service method
        String result = userService.changePassword("john_doe", "old_password", "new_password");

        // Assert the results
        assertEquals("Password has been successfully updated for user john_doe", result);
        assertEquals("encoded_new_password", user.getPassword());

        // Verify the interactions
        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(passwordEncoder, times(1)).matches("old_password", "encoded_password");
        verify(passwordEncoder, times(1)).encode("new_password");
        verify(userRepository, times(1)).save(user);
    }


    // Test for changing password when user not found
    @Test
    void testChangePasswordUserNotFound() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.changePassword("unknown_user", "old_password", "new_password"));
        verify(userRepository, times(1)).findByUsername("unknown_user");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // Test for changing password with incorrect old password
    @Test
    void testChangePasswordInvalidOldPassword() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_old_password", user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.changePassword("john_doe", "wrong_old_password", "new_password"));
        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(passwordEncoder, times(1)).matches("wrong_old_password", user.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}