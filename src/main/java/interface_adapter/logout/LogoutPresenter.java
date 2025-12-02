package interface_adapter.logout;

import interface_adapter.ViewManagerModel;
import use_case.logout.LogoutOutputBoundary;
import interface_adapter.auth.AuthViewModel;

public class LogoutPresenter implements LogoutOutputBoundary {
    private AuthViewModel authViewModel;
    private ViewManagerModel viewManagerModel;

    public LogoutPresenter(AuthViewModel authViewmodel, ViewManagerModel viewManagerModel) {
        this.authViewModel = authViewmodel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView() {
        this.viewManagerModel.setState(authViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
