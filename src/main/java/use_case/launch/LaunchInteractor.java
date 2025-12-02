package use_case.launch;

import data_access.SteamGameLauncher;
import entity.Game;

import javax.swing.*;

public class LaunchInteractor implements LaunchInputBoundary {

    private final LaunchOutputBoundary launchOutputBoundary;
    private final SteamGameLauncher launcher;

    public LaunchInteractor(SteamGameLauncher launcher, LaunchOutputBoundary presenter) {
        this.launchOutputBoundary = presenter;
        this.launcher = launcher;
    }

    @Override
    public void execute(Game selectedGame) {

        if (selectedGame == null) {
            launchOutputBoundary.showGameNotFoundFailure("Game not found.");
            return;
        }

        long appId = selectedGame.getId();
        boolean success = launcher.launchGame(appId, null);

        if (success) {
            launchOutputBoundary.showLaunchSuccess(
                    "Launching " + selectedGame.getTitle()
            );
        } else {
            launchOutputBoundary.showLaunchFailure(
                    "Failed to launch " + selectedGame.getTitle()
            );
        }
    }

}
