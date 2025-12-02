package interface_adapter.loggedin;

import use_case.refresh.RefreshOutputBoundary;
import interface_adapter.auth.AuthViewModel;
import interface_adapter.ViewManagerModel;

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
    public void prepareSuccessView(LoggedinState state) {
        loggedinViewModel.setState(state);
        loggedinViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(String errMsg) {
        LoggedinState newState = new LoggedinState();
        newState.setError(errMsg);
        loggedinViewModel.setState(newState);
        loggedinViewModel.firePropertyChange();

        viewManagerModel.setState(authViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
