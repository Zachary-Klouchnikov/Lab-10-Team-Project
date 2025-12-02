package view;

import entity.Game;
import entity.User;
import interface_adapter.loggedin.LoggedinViewModel;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoggedinViewTest {

    /**
     * Recursively finds the first JButton with the given text.
     */
    private JButton findButtonByText(Component c, String text) {
        if (c instanceof JButton b && text.equals(b.getText())) {
            return b;
        }
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                JButton found = findButtonByText(child, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Recursively finds the first JList in the component tree.
     */
    @SuppressWarnings("rawtypes")
    private JList findFirstJList(Component c) {
        if (c instanceof JList<?> list) {
            return list;
        }
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                JList<?> found = findFirstJList(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    @Test
    void launchIsEnabled() {
        LoggedinViewModel viewModel = new LoggedinViewModel();
        LoggedinView view = new LoggedinView(viewModel);

        JButton launchButton = findButtonByText(view, "Launch");
        assertNotNull(launchButton, "Launch button should be present in LoggedinView");
        assertTrue(launchButton.isEnabled(), "Launch button should be enabled by default");
    }

    @Test
    void clickingLaunchButtonWithoutSelectionRuns() {
        LoggedinViewModel viewModel = new LoggedinViewModel();
        LoggedinView view = new LoggedinView(viewModel);

        JButton launchButton = findButtonByText(view, "Launch");
        assertNotNull(launchButton, "Launch button should be present in LoggedinView");

        assertDoesNotThrow(() -> launchButton.doClick(),
                "Clicking Launch without selecting a game should not throw any exception");
    }

    @Test
    void clickingLaunchButtonWithSelectedGameRuns() {
        long expectedAppId = 12345L;
        Game game = new Game(expectedAppId, "Test Game", 0, "hash", 0);
        List<Game> library = List.of(game);
        User user = new User(1L, "TestUser", new ArrayList<>(), library, "userhash");

        LoggedinViewModel viewModel = new LoggedinViewModel();
        LoggedinView view = new LoggedinView(viewModel);
        view.setUser(user);

        @SuppressWarnings("rawtypes")
        JList gameList = findFirstJList(view);
        assertNotNull(gameList, "Game list should exist in LoggedinView");
        assertTrue(gameList.getModel().getSize() > 0, "Game list should not be empty");

        gameList.setSelectedIndex(0);

        JButton launchButton = findButtonByText(view, "Launch");
        assertNotNull(launchButton, "Launch button should be present in LoggedinView");

        assertDoesNotThrow(() -> launchButton.doClick(),
                "Clicking Launch with a selected game should not throw any exception");
    }
}
