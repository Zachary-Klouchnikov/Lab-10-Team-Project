package interface_adapter.loggedin;

import use_case.launch.LaunchOutputBoundary;

import javax.swing.*;

public class LaunchPresenter implements LaunchOutputBoundary {

    @Override
    public void showLaunchSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Game Launch", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showGameNotFoundFailure(String message) {
        JOptionPane.showMessageDialog(null, message, "Game Not Found", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showLaunchFailure(String message) {
        JOptionPane.showMessageDialog(null, message, "Launch Error", JOptionPane.ERROR_MESSAGE);
    }
}
