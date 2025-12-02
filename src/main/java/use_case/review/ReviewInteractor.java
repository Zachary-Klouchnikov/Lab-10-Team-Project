package use_case.review;

import data_access.ReviewAccessObject;
import entity.SessionManager;
import entity.User;
import use_case.refresh.RefreshOutputBoundary;

import java.util.ArrayList;
import java.util.List;

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
}
