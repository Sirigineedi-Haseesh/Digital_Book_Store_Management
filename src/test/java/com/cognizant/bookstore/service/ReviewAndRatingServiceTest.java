package com.cognizant.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.cognizant.bookstore.dto.ReviewAndRatingDTO;
import com.cognizant.bookstore.model.Book;
import com.cognizant.bookstore.model.ReviewAndRating;
import com.cognizant.bookstore.model.User;
import com.cognizant.bookstore.repository.BookRepository;
import com.cognizant.bookstore.repository.ReviewAndRatingRepository;
import com.cognizant.bookstore.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ReviewAndRatingServiceTest {

	@Mock
	private ReviewAndRatingRepository reviewAndRatingRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private UserRepository userRepository;
//    @Autowired
//    private ModelMapper modelMapper;

	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private ReviewAndRatingService reviewAndRatingService;

	private ReviewAndRating reviewAndRating;
	private ReviewAndRatingDTO reviewAndRatingDTO;
	private Book book;
	private User user;

	@BeforeEach
	public void setUp() {
		// Mock User
		user = new User();
		user.setUserId(1L);
		user.setUserName("Test User");

		// Mock Book
		book = new Book();
		book.setBookId(1L);
		book.setTitle("Test Book");

		// Mock ReviewAndRating
		reviewAndRating = new ReviewAndRating();
		reviewAndRating.setReviewId(1);
		reviewAndRating.setRating(5);
		reviewAndRating.setReview("Great book!");
		reviewAndRating.setDate(null); // Assign a valid date if required
		reviewAndRating.setBook(book);
		reviewAndRating.setUser(user);

		// Mock ReviewAndRatingDTO
		reviewAndRatingDTO = new ReviewAndRatingDTO();
		reviewAndRatingDTO.setReviewId(1);
		reviewAndRatingDTO.setRating(5);
		reviewAndRatingDTO.setReview("Great book!");
		reviewAndRatingDTO.setDate(null); // Assign a valid date if required
		reviewAndRatingDTO.setBookTitle("Test Book");
		reviewAndRatingDTO.setUserName("Test User");
	}
	@Test
	public void testAddReviewAndRating() {
	    // Mocking repositories and ModelMapper
	    when(bookRepository.findByTitle("Test Book")).thenReturn(Optional.of(book));
	    when(userRepository.findByUserName("Test User")).thenReturn(Optional.of(user));
	    when(reviewAndRatingRepository.save(any(ReviewAndRating.class))).thenReturn(reviewAndRating);

	    // Correctly stub ModelMapper
	    when(modelMapper.map(eq(reviewAndRating), eq(ReviewAndRatingDTO.class))).thenReturn(reviewAndRatingDTO);

	    // Call the service method
	    ReviewAndRatingDTO result = reviewAndRatingService.addReviewAndRating(reviewAndRatingDTO);

	    // Assertions
	    assertNotNull(result);
	    assertEquals(reviewAndRatingDTO, result); // Correct comparison using updated equals method
	    verify(bookRepository, times(1)).findByTitle("Test Book");
	    verify(userRepository, times(1)).findByUserName("Test User");
	    verify(reviewAndRatingRepository, times(1)).save(any(ReviewAndRating.class));
	    verify(modelMapper, times(1)).map(eq(reviewAndRating), eq(ReviewAndRatingDTO.class));
	}


	@Test
	public void testGetAllReviewsAndRatings() {
		// Mock the repository call to find all reviews and ratings
		when(reviewAndRatingRepository.findAll()).thenReturn(Arrays.asList(reviewAndRating));

		// Only include modelMapper stubbing if convertEntityToDTO explicitly calls
		// modelMapper.map

		lenient().when(modelMapper.map(reviewAndRating, ReviewAndRatingDTO.class)).thenReturn(reviewAndRatingDTO);

		// Call the service method
		List<ReviewAndRatingDTO> result = reviewAndRatingService.getAllReviewsAndRatings();

		// Assertions
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(reviewAndRatingDTO.getReviewId(), result.get(0).getReviewId());
		verify(reviewAndRatingRepository, times(1)).findAll();
	}

	@Test
	public void testGetReviewAndRatingById() {
		when(reviewAndRatingRepository.findById(1)).thenReturn(Optional.of(reviewAndRating));

		ReviewAndRatingDTO result = reviewAndRatingService.getReviewAndRatingById(1);

		assertNotNull(result);
		assertEquals(reviewAndRatingDTO.getReviewId(), result.getReviewId());
		verify(reviewAndRatingRepository, times(1)).findById(1);
	}

	@Test
	public void testGetReviewAndRatingByIdNotFound() {
		when(reviewAndRatingRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> reviewAndRatingService.getReviewAndRatingById(1));

		assertEquals("Review with ID 1 not found", exception.getMessage());
		verify(reviewAndRatingRepository, times(1)).findById(1);
	}

	@Test
	public void testUpdateReviewAndRating() {
	    // Mocking repository and model calls
	    when(reviewAndRatingRepository.existsById(1)).thenReturn(true);
	    when(bookRepository.findByTitle("Test Book")).thenReturn(Optional.of(book));
	    when(userRepository.findByUserName("Test User")).thenReturn(Optional.of(user));
	    when(reviewAndRatingRepository.save(any(ReviewAndRating.class))).thenReturn(reviewAndRating);

	    // Ensure proper stubbing
	    when(modelMapper.map(eq(reviewAndRating), eq(ReviewAndRatingDTO.class))).thenReturn(reviewAndRatingDTO);

	    // Call the service method
	    ReviewAndRatingDTO result = reviewAndRatingService.updateReviewAndRating(reviewAndRatingDTO);

	    // Assertions
	    assertNotNull(result);
	    assertEquals(reviewAndRatingDTO, result); // Correct comparison using updated equals method
	    verify(reviewAndRatingRepository, times(1)).existsById(1);
	    verify(reviewAndRatingRepository, times(1)).save(any(ReviewAndRating.class));
	}

	@Test
	public void testUpdateReviewAndRatingNotFound() {
		when(reviewAndRatingRepository.existsById(1)).thenReturn(false);

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> reviewAndRatingService.updateReviewAndRating(reviewAndRatingDTO));

		assertEquals("Review with ID 1 not found", exception.getMessage());
		verify(reviewAndRatingRepository, times(1)).existsById(1);
		verifyNoMoreInteractions(reviewAndRatingRepository);
	}

	@Test
	public void testDeleteReviewAndRating() {
		when(reviewAndRatingRepository.existsById(1)).thenReturn(true);

		reviewAndRatingService.deleteReviewAndRating(1);

		verify(reviewAndRatingRepository, times(1)).existsById(1);
		verify(reviewAndRatingRepository, times(1)).deleteById(1);
	}

	@Test
	public void testDeleteReviewAndRatingNotFound() {
		when(reviewAndRatingRepository.existsById(1)).thenReturn(false);

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> reviewAndRatingService.deleteReviewAndRating(1));

		assertEquals("Review with ID 1 not found", exception.getMessage());
		verify(reviewAndRatingRepository, times(1)).existsById(1);
		verifyNoMoreInteractions(reviewAndRatingRepository);
	}
}
