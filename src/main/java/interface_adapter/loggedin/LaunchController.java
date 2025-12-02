package interface_adapter.loggedin;

import entity.Game;
import use_case.launch.LaunchInputBoundary;

import javax.swing.*;

public class LaunchController {
    private LaunchInputBoundary launchInputBoundary;

    public LaunchController(LaunchInputBoundary lib) {
        this.launchInputBoundary = lib;
    }

    public void execute(Game selectedGame) {
        launchInputBoundary.execute(selectedGame);
    }
}
