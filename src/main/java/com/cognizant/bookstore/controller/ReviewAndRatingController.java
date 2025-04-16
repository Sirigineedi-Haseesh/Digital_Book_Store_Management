//package com.cognizant.bookstore.controller;
// 
//import java.util.List;
// 
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
// 
//import com.cognizant.bookstore.dto.ReviewAndRatingDTO;
//import com.cognizant.bookstore.service.ReviewAndRatingService;
// 
//@RestController
//@RequestMapping("/reviews-and-ratings")
//public class ReviewAndRatingController {
// 
//    @Autowired
//    private ReviewAndRatingService service;
// 
//    @PostMapping("/addReviewRating")
//    public ReviewAndRatingDTO addReviewAndRating(@RequestBody ReviewAndRatingDTO reviewAndRatingDTO) {
//        return service.addReviewAndRating(reviewAndRatingDTO);
//    }
// 
//    @GetMapping("/getallReviewandRating")
//    public List<ReviewAndRatingDTO> getAllReviewsAndRatings() {
//        return service.getAllReviewsAndRatings();
//    }
// 
//    @GetMapping("/{id}")
//    public ReviewAndRatingDTO getReviewAndRatingById(@PathVariable int id) {
//        return service.getReviewAndRatingById(id);
//    }
// 
//    @PutMapping("/updateReview")
//    public ReviewAndRatingDTO updateReviewAndRating(@RequestBody ReviewAndRatingDTO reviewAndRatingDTO) {
//        return service.updateReviewAndRating(reviewAndRatingDTO);
//    }
// 
//    @DeleteMapping("/{id}")
//    public String deleteReviewAndRating(@PathVariable int id) {
//        service.deleteReviewAndRating(id);
//        return "Review and rating deleted successfully!";
//    }
//    
////    @PatchMapping("/patchReview/{id}")
////    public ReviewAndRatingDTO patchReviewAndRating(@PathVariable int id, @RequestBody ReviewAndRatingDTO dto) {
////        return service.updateReviewAndRatingFields(id, dto);
////    }
//}
// 
// 