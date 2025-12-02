package interface_adapter.compareusers;

import interface_adapter.ViewManagerModel;
import interface_adapter.loggedin.LoggedinState;
import interface_adapter.loggedin.LoggedinViewModel;
import use_case.compareusers.CompareUsersOutputBoundary;
import use_case.compareusers.CompareUsersOutputData;
import entity.User;

public class CompareUsersPresenter implements CompareUsersOutputBoundary {
    private final CompareUsersViewModel comparisonViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedinViewModel loggedinViewModel;

    public CompareUsersPresenter(CompareUsersViewModel comparisonViewModel,
                                 ViewManagerModel viewManagerModel,
                                 LoggedinViewModel loggedinViewModel) {
        this.comparisonViewModel = comparisonViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedinViewModel = loggedinViewModel;
    }

    @Override
    public void presentComparison(CompareUsersOutputData outputData) {
        CompareUsersState state = comparisonViewModel.getState();
        state.setLoggedInUser(outputData.getLoggedInUser());
        state.setComparisonUser(outputData.getComparisonUser());
        state.setErrorMessage("");
        comparisonViewModel.firePropertyChange();

        viewManagerModel.setState(comparisonViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void presentLoggedIn(User loggedInUser) {
        LoggedinState loggedinState = loggedinViewModel.getState();
        loggedinState.setUser(loggedInUser);
        loggedinState.setError("");
        loggedinViewModel.firePropertyChange();

        viewManagerModel.setState(loggedinViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void presentFailure(String errorMessage) {
        CompareUsersState state = comparisonViewModel.getState();
        state.setErrorMessage(errorMessage);
        comparisonViewModel.firePropertyChange();
    }
}
