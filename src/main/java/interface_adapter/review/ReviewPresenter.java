package interface_adapter.review;


import entity.User;
import interface_adapter.ViewManagerModel;
import use_case.review.ReviewInputBoundary;
import use_case.review.ReviewOutputBoundary;
import view.ReviewPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewPresenter implements ReviewOutputBoundary {
    private final ReviewViewModel reviewViewModel;
    private final ViewManagerModel viewManagerModel;

    public ReviewPresenter(ReviewViewModel rvm, ViewManagerModel vmm) {
        this.reviewViewModel = rvm;
        this.viewManagerModel = vmm;
    }

    @Override
    public void prepareSuccessView(ArrayList<String> lst) {
        JFrame frame = new JFrame("Reviews");
        ReviewPanel reviewPanel = new ReviewPanel(lst);
        frame.add(reviewPanel);

        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.reviewViewModel.firePropertyChange();

        this.viewManagerModel.setState(reviewViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
