package view;

import entity.Game;
import entity.User;
import interface_adapter.userstatistics.UserStatisticsViewModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsPanelSmokeTest {

    private User sampleUser() {
        Game game = new Game(1L, "Test Game", 120, "hash", 60);
        return new User(10L, "tester", List.of(), List.of(game), "profile");
    }

    @Test
    void createForUserBuildsPanel() {
        System.setProperty("java.awt.headless", "true");
        UserStatisticsPanel panel = UserStatisticsPanel.createForUser(sampleUser());
        assertNotNull(panel);
        assertTrue(panel.getComponentCount() >= 1);
    }

    @Test
    void propertyChangeWithErrorShowsDialogSafely() {
        System.setProperty("java.awt.headless", "true");
        UserStatisticsViewModel vm = new UserStatisticsViewModel();
        UserStatisticsPanel panel = new UserStatisticsPanel(vm);
        vm.getState().setErrorMessage("err");
        panel.propertyChange(new java.beans.PropertyChangeEvent(vm, "state", null, vm.getState()));
        assertNotNull(panel);
    }
}
