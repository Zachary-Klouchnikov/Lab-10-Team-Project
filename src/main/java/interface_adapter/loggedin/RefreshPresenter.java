package interface_adapter.loggedin;

import use_case.refresh.RefreshOutputBoundary;
import interface_adapter.auth.AuthViewModel;
import interface_adapter.ViewManagerModel;
import entity.User;

public class RefreshPresenter implements RefreshOutputBoundary{
    private LoggedinViewModel loggedinViewModel;
    private AuthViewModel authViewModel;
    private ViewManagerModel viewManagerModel;

    public RefreshPresenter(LoggedinViewModel loggedinViewModel, AuthViewModel authViewModel, ViewManagerModel viewManagerModel) {
        this.loggedinViewModel = loggedinViewModel;
        this.authViewModel = authViewModel;
        this.viewManagerModel = viewManagerModel;
    }   

    @Override
    public void prepareSuccessView(User user) {
        LoggedinState newState = loggedinViewModel.getState();
        newState.setUser(user);
        loggedinViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(String errMsg) {
        LoggedinState newState = loggedinViewModel.getState();
        newState.setError(errMsg);       
        newState.setUser(null);
        loggedinViewModel.firePropertyChange();

        viewManagerModel.setState(authViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
