package use_case.review;

import data_access.ReviewAccessObject;
import entity.User;

import java.util.ArrayList;

public class ReviewInteractor implements ReviewInputBoundary {
    private ReviewOutputBoundary reviewOutputBoundary;
    private ReviewAccessObject reviewDAO;

    public ReviewInteractor(ReviewOutputBoundary reviewOutputBoundary) {
        this.reviewOutputBoundary = reviewOutputBoundary;
        this.reviewDAO = new ReviewAccessObject();
    }

    @Override
    public void execute(User user) {
        ArrayList<String> lst = reviewDAO.ReviewDisplay(user);
        reviewOutputBoundary.prepareSuccessView(lst);
    }
    @Override
    public void backToLoggedIn(User user) {
        reviewOutputBoundary.presentLoggedIn(user);
    }
}
