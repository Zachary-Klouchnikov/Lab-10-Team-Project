package interface_adapter.auth;

import use_case.auth.AuthOutputBoundary;
import use_case.auth.AuthOutputData;
import interface_adapter.ViewManagerModel;
import interface_adapter.loggedin.LoggedinState;
import interface_adapter.loggedin.LoggedinViewModel;
import entity.User;

public class AuthPresenter implements AuthOutputBoundary {
    private final AuthViewModel authViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedinViewModel loggedinViewModel;

    public AuthPresenter(AuthViewModel avm, LoggedinViewModel lvm, ViewManagerModel vmm) {
        this.authViewModel = avm;
        this.loggedinViewModel = lvm;
        this.viewManagerModel = vmm;
    }

    @Override
    public void prepareSuccessView(AuthOutputData outputData) {
        User user = outputData.getUser();
        LoggedinState state = loggedinViewModel.getState();       
        state.setUser(user);
        state.setError("");

        authViewModel.setState(new AuthState());

        this.loggedinViewModel.setState(state);
        this.viewManagerModel.setState(loggedinViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        final AuthState state = authViewModel.getState();
        state.setErrorMessage(errorMessage);
        authViewModel.firePropertyChange();
    }
}
