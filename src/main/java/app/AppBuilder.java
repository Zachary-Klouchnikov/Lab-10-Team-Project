package app;

import java.awt.CardLayout;
import javax.swing.*;

import data_access.*;
import interface_adapter.*;
import interface_adapter.auth.*;
import interface_adapter.loggedin.*;
import interface_adapter.logout.*;
import use_case.auth.*;
import use_case.logout.*;
import use_case.refresh.*;
import view.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    private UserDataAccessObject userDAO = new UserDataAccessObject();
    private AuthDataAccessObject authDAO = new AuthDataAccessObject();

    private AuthView authView;
    private LoggedinView loggedin;
    private AuthViewModel authViewModel;
    private LoggedinViewModel loggedinModel;

    public AppBuilder () {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addAuthView() {
        authViewModel = new AuthViewModel();
        authView = new AuthView(authViewModel);
        cardPanel.add(authView, authView.getViewName());
        return this;
    }

    public AppBuilder addLoggedinView() {
        loggedinModel = new LoggedinViewModel();
        loggedin = new LoggedinView(loggedinModel);
        cardPanel.add(loggedin, loggedin.getViewName());
        return this;
    }

    public AppBuilder addAuthUseCase() {
        final AuthOutputBoundary outputBoundary = new AuthPresenter(authViewModel, loggedinModel, viewManagerModel);
        final AuthInputBoundary inputBoundary = new AuthInteractor(outputBoundary, authDAO, userDAO);
        AuthController authController = new AuthController(inputBoundary);
        authView.setAuthController(authController);
        return this;
    }

    public AppBuilder addRefreshUseCase() {
        final RefreshOutputBoundary outputBoundary = new RefreshPresenter(loggedinModel, authViewModel, viewManagerModel);
        final RefreshInputBoundary inputBoundary = new RefreshInteractor(outputBoundary);
        RefreshController refreshController = new RefreshController(inputBoundary);

        loggedin.setRefreshController(refreshController);
        return this;
    }

    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary outputBoundary = new LogoutPresenter(authViewModel, viewManagerModel);
        final LogoutInputBoundary inputBoundary = new LogoutInteractor(outputBoundary);
        LogoutController logoutController = new LogoutController(inputBoundary);
        loggedin.setLogoutController(logoutController);
        return this;
    }

    public JFrame build() {
        final JFrame app = new JFrame("Steam-Wrapped");
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.add(cardPanel);
        
        viewManagerModel.setState(authView.getViewName());
        viewManagerModel.firePropertyChange();

        return app;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Check if API key is set (optional warning)
            if (System.getenv("APIKEY") == null || System.getenv("APIKEY").isEmpty()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Steam API key (APIKEY environment variable) is not set."
                );
                System.exit(0);
            }
        });

        JFrame app = new AppBuilder()
            .addAuthView()
            .addLoggedinView()
            .addAuthUseCase()
            .addRefreshUseCase()
            .addLogoutUseCase()
            .build();

        app.pack();
        app.setLocationRelativeTo(null);
        app.setVisible(true);
    }
}
