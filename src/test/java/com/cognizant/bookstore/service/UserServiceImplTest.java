package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cognizant.bookstore.dto.UserLoginDTO;
import com.cognizant.bookstore.dto.UserProfileDTO;
import com.cognizant.bookstore.dto.UserRegisterDTO;
import com.cognizant.bookstore.exceptions.UserNotFoundException;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserProfileDTO userProfileDTO;
    private UserRegisterDTO userRegisterDTO;
    private UserLoginDTO userLoginDTO;

    @BeforeEach
    public void setUp() {
        // Mock User entity
        user = new User();
        user.setUserId(1L);
        user.setUserName("testuser");
        user.setPassword("password123");
        user.setRole("USER");

        // Mock UserProfileDTO
        userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUserId(1L);
        userProfileDTO.setUserName("testuser");
        userProfileDTO.setRole("USER");

        // Mock UserRegisterDTO
        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUserName("testuser");
        userRegisterDTO.setPassword("password123");

        // Mock UserLoginDTO
        userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserName("testuser");
        userLoginDTO.setPassword("password123");
    }

    @Test
    public void testRegisterUser() {
        when(modelMapper.map(userRegisterDTO, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserProfileDTO.class)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.registerUser(userRegisterDTO);

        assertNotNull(result);
        assertEquals(userProfileDTO, result);
        verify(userRepository, times(1)).save(user);
        verify(modelMapper, times(1)).map(userRegisterDTO, User.class);
        verify(modelMapper, times(1)).map(user, UserProfileDTO.class);
    }

    @Test
    public void testLoginUserSuccess() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));

        String result = userService.loginUser(userLoginDTO);

        assertEquals("Login successful", result);
        verify(userRepository, times(1)).findByUserName("testuser");
    }

    @Test
    public void testLoginUserInvalidCredentials() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));

        userLoginDTO.setPassword("wrongpassword");

        String result = userService.loginUser(userLoginDTO);

        assertEquals("Invalid credentials", result);
        verify(userRepository, times(1)).findByUserName("testuser");
    }

    @Test
    public void testGetUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserProfileDTO.class)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.getUserProfile(1L);

        assertNotNull(result);
        assertEquals(userProfileDTO, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUserProfileNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserProfile(1L);
        });

        assertEquals("User with ID 1 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateUserProfile() {
        // Mock repository calls
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Adjust ModelMapper stubbing to handle dynamic arguments
        lenient().when(modelMapper.map(any(UserProfileDTO.class), eq(User.class))).thenReturn(user);
        lenient().when(modelMapper.map(any(User.class), eq(UserProfileDTO.class))).thenReturn(userProfileDTO);

        // Call the service method
        UserProfileDTO result = userService.updateUserProfile(1L, userProfileDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(userProfileDTO, result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        verify(modelMapper, times(1)).map(userProfileDTO, User.class);
        verify(modelMapper, times(1)).map(user, UserProfileDTO.class);
    }


    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(modelMapper.map(user, UserProfileDTO.class)).thenReturn(userProfileDTO);

        List<UserProfileDTO> result = userService.getAllUser();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userProfileDTO, result.get(0));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String result = userService.deleteUser(1L);

        assertEquals("User with username testuser has been deleted successfully.", result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }
}
