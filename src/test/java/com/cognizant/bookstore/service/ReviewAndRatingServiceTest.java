package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cognizant.bookstore.dto.ReviewAndRatingDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.ReviewAndRating;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.ReviewAndRatingRepository;
import com.cognizant.bookstore.repository.UserRepository;
import com.cognizant.bookstore.service.ReviewAndRatingService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Collections;
import java.util.List;

class ReviewAndRatingServiceTest {

    @InjectMocks
    private ReviewAndRatingService service;

    @Mock
    private ReviewAndRatingRepository reviewAndRatingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddReviewAndRating_success() {
        // Arrange
        ReviewAndRatingDTO dto = new ReviewAndRatingDTO();
        dto.setBookId(1L);
        dto.setUserId(1L);
        dto.setReview("Great book!");
        dto.setRating(5);
        dto.setDate(LocalDateTime.now());

        Book mockBook = new Book();
        mockBook.setBookId(1L);

        User mockUser = new User();
        mockUser.setUserId(1L);

        ReviewAndRating mockReview = new ReviewAndRating();
        mockReview.setReview(dto.getReview());
        mockReview.setRating(dto.getRating());
        mockReview.setDate(dto.getDate());
        mockReview.setBook(mockBook);
        mockReview.setUser(mockUser);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(modelMapper.map(dto, ReviewAndRating.class)).thenReturn(mockReview);
        when(reviewAndRatingRepository.save(mockReview)).thenReturn(mockReview);
        when(modelMapper.map(mockReview, ReviewAndRatingDTO.class)).thenReturn(dto);

        // Act
        ReviewAndRatingDTO result = service.addReviewAndRating(dto);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(dto.getReview(), result.getReview());
        assertEquals(dto.getRating(), result.getRating());
        verify(reviewAndRatingRepository, times(1)).save(mockReview);
    }

    @Test
    void testGetReviewAndRatingById_success() {
        // Arrange
        int reviewId = 1;
        ReviewAndRating mockReview = new ReviewAndRating();
        mockReview.setReviewId(reviewId);
        mockReview.setReview("Excellent book!");
        mockReview.setRating(5);

        ReviewAndRatingDTO mockDTO = new ReviewAndRatingDTO();
        mockDTO.setReviewId(reviewId);
        mockDTO.setReview("Excellent book!");
        mockDTO.setRating(5);

        when(reviewAndRatingRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
        when(modelMapper.map(mockReview, ReviewAndRatingDTO.class)).thenReturn(mockDTO);

        // Act
        ReviewAndRatingDTO result = service.getReviewAndRatingById(reviewId);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(mockDTO.getReview(), result.getReview());
        assertEquals(mockDTO.getRating(), result.getRating());
        verify(reviewAndRatingRepository, times(1)).findById(reviewId);
    }

    @Test
    void testGetAllReviewsAndRatings_success() {
        // Arrange
        ReviewAndRating mockReview = new ReviewAndRating();
        mockReview.setReview("Awesome read!");
        mockReview.setRating(4);

        ReviewAndRatingDTO mockDTO = new ReviewAndRatingDTO();
        mockDTO.setReview("Awesome read!");
        mockDTO.setRating(4);

        when(reviewAndRatingRepository.findAll()).thenReturn(Collections.singletonList(mockReview));
        when(modelMapper.map(mockReview, ReviewAndRatingDTO.class)).thenReturn(mockDTO);

        // Act
        List<ReviewAndRatingDTO> result = service.getAllReviewsAndRatings();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result size should be 1");
        assertEquals(mockDTO.getReview(), result.get(0).getReview());
        verify(reviewAndRatingRepository, times(1)).findAll();
    }

    @Test
    void testUpdateReviewAndRating_success() {
        // Arrange
        ReviewAndRatingDTO dto = new ReviewAndRatingDTO();
        dto.setReviewId(1);
        dto.setBookId(1L);
        dto.setUserId(1L);
        dto.setReview("Updated review!");
        dto.setRating(4);

        ReviewAndRating mockReview = new ReviewAndRating();
        mockReview.setReview("Updated review!");
        mockReview.setRating(4);

        Book mockBook = new Book();
        mockBook.setBookId(1L);

        User mockUser = new User();
        mockUser.setUserId(1L);

        when(reviewAndRatingRepository.existsById(1)).thenReturn(true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(modelMapper.map(dto, ReviewAndRating.class)).thenReturn(mockReview);
        when(reviewAndRatingRepository.save(mockReview)).thenReturn(mockReview);
        when(modelMapper.map(mockReview, ReviewAndRatingDTO.class)).thenReturn(dto);

        // Act
        ReviewAndRatingDTO result = service.updateReviewAndRating(dto);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(dto.getReview(), result.getReview());
        assertEquals(dto.getRating(), result.getRating());
        verify(reviewAndRatingRepository, times(1)).save(mockReview);
    }

    @Test
    void testDeleteReviewAndRating_success() {
        // Arrange
        int reviewId = 1;

        when(reviewAndRatingRepository.existsById(reviewId)).thenReturn(true);

        // Act
        service.deleteReviewAndRating(reviewId);

        // Assert
        verify(reviewAndRatingRepository, times(1)).deleteById(reviewId);
    }
     
}
