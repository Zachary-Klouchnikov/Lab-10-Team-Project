package use_case.auth;

import entity.User;
import entity.SessionManager;
import data_access.UserDataAccessObject;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;
import java.awt.*;

public class AuthInteractor implements AuthInputBoundary {
    private final AuthOutputBoundary authOutputBoundary;
    private final AuthDataAccessInterface authDataAccessInterface;
    private final UserDataAccessObject userDataAccessObject;

    public AuthInteractor(AuthOutputBoundary authOutputBoundary, AuthDataAccessInterface authDataAccessInterface, UserDataAccessObject userDAO) {
        this.authOutputBoundary = authOutputBoundary;
        this.authDataAccessInterface = authDataAccessInterface;
        this.userDataAccessObject = userDAO;
    }

    @Override
    public void execute(AuthInputData input) {
        JLabel status = input.getStatus();
        JButton button = input.getButton();
        JProgressBar bar = input.getBar();

        bar.setVisible(true);
        button.setEnabled(false);
        status.setText("Opening Steam login in browser...");
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                CompletableFuture<Long> handle = authDataAccessInterface.authenticate();

                SwingUtilities.invokeLater(() ->
                    status.setText("Waiting for Steam authentication...")
                );

                Long steamId = handle.get();

                SwingUtilities.invokeLater(() ->
                    status.setText("Fetching user profile...")
                );

                User user = userDataAccessObject.get(steamId);
                SessionManager.getInstance().createSession(steamId, user);

                return user;
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    status.setText("Login successful!");
                    bar.setVisible(false);

                    authOutputBoundary.prepareSuccessView(new AuthOutputData(user));
                } catch (Exception e) {
                    String errMsg = "Login Failed: " + e.getMessage();
                    status.setText(errMsg);
                    status.setForeground(new Color(255, 100, 100));
                    bar.setVisible(false);
                    button.setEnabled(true);
                    Timer timer = new Timer(3000, evt -> {
                        status.setForeground(Color.WHITE);
                        status.setText(" ");
                    });
                    timer.setRepeats(false);
                    timer.start();

                    authOutputBoundary.prepareFailureView(errMsg);
                }

            }
        };
        worker.execute();

        authDataAccessInterface.close();
    }
}
