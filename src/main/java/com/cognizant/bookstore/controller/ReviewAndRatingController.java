//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/reviews-and-ratings")
//public class ReviewAndRatingController {
//
//    @Autowired
//    private ReviewAndRatingService service;
//
//    @PostMapping
//    public ReviewAndRating addReviewAndRating(@RequestBody ReviewAndRating reviewAndRating) {
//        return service.addReviewAndRating(reviewAndRating);
//    }
//
//    @GetMapping
//    public List<ReviewAndRating> getAllReviewsAndRatings() {
//        return service.getAllReviewsAndRatings();
//    }
//
//    @GetMapping("/{id}")
//    public ReviewAndRating getReviewAndRatingById(@PathVariable int id) {
//        return service.getReviewAndRatingById(id);
//    }
//
//    @PutMapping
//    public ReviewAndRating updateReviewAndRating(@RequestBody ReviewAndRating reviewAndRating) {
//        return service.updateReviewAndRating(reviewAndRating);
//    }
//
//    @DeleteMapping("/{id}")
//    public String deleteReviewAndRating(@PathVariable int id) {
//        service.deleteReviewAndRating(id);
//        return "Review and rating deleted successfully!";
//    }
//}
