package interface_adapter.review;


import entity.User;
import interface_adapter.ViewManagerModel;
import interface_adapter.loggedin.LoggedinState;
import interface_adapter.loggedin.LoggedinViewModel;
import use_case.review.ReviewOutputBoundary;
import view.ReviewView;

import javax.swing.*;
import java.util.ArrayList;

public class ReviewPresenter implements ReviewOutputBoundary {
    private final ReviewViewModel reviewViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedinViewModel loggedinViewModel;

    public ReviewPresenter(ReviewViewModel rvm, ViewManagerModel vmm, LoggedinViewModel lvm) {
        this.reviewViewModel = rvm;
        this.viewManagerModel = vmm;
        this.loggedinViewModel = lvm;
    }

    @Override
    public void prepareSuccessView(ArrayList<String> lst) {
        JFrame frame = new JFrame("Reviews");
        ReviewView reviewPanel = new ReviewView(reviewViewModel);
        frame.add(reviewPanel);

        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        reviewViewModel.firePropertyChange();

        viewManagerModel.setState(reviewViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
    @Override
    public void presentLoggedIn(User user) {
        LoggedinState loggedinState = loggedinViewModel.getState();
        loggedinState.setUser(user);
        loggedinState.setError("");
        loggedinViewModel.firePropertyChange();

        viewManagerModel.setState(loggedinViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
