package interface_adapter.auth;

import use_case.auth.AuthOutputBoundary;
import use_case.auth.AuthOutputData;
import interface_adapter.ViewManagerModel;
import entity.User;

public class AuthPresenter implements AuthOutputBoundary {
    private final AuthViewModel authViewModel;
    private final ViewManagerModel viewManagerModel;

    public AuthPresenter(AuthViewModel avm, ViewManagerModel vmm) {
        this.authViewModel = avm;
        this.viewManagerModel = vmm;
    }

    @Override
    public void prepareSuccessView(AuthOutputData outputData) {
        User user = outputData.getUser();

        // Pass user object into otherView's state or smth.
        // this.viewManagerModel.setState(); <-- Change to some other View.
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        final AuthState state = authViewModel.getState();
        state.setErrorMessage(errorMessage);
        authViewModel.firePropertyChange();
    }
}
