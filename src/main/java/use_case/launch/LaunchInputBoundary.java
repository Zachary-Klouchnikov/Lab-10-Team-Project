package use_case.launch;

import entity.Game;

import javax.swing.*;

public interface LaunchInputBoundary {
    void execute(JLabel selectedLabel, Game selectedGame);
}
