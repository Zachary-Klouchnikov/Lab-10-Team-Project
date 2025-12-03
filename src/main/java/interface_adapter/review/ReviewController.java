package interface_adapter.review;


import entity.User;
import use_case.review.ReviewInputBoundary;

public class ReviewController {

    private ReviewInputBoundary reviewInputBoundary;

    public ReviewController(ReviewInputBoundary reviewInputBoundary) {
        this.reviewInputBoundary = reviewInputBoundary;
    }

    public void execute(User user) {
        this.reviewInputBoundary.execute(user);
    }

    public void backToLoggedIn(User loggedInUser) {
        reviewInputBoundary.backToLoggedIn(loggedInUser);
    }
}
