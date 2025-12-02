package view;

import entity.User;
import entity.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonPanelTest {

    private User mainUser;
    private User friend1;
    private User friend2;
    private ComparisonPanel panel;

    @BeforeEach
    void setup() {
        Game g = new Game(1L, "G", 10, "t", 5);

        friend1 = new User(2L, "Alice", new ArrayList<>(), List.of(g), "p1");
        friend2 = new User(3L, "Bob",   new ArrayList<>(), List.of(g), "p2");

        mainUser = new User(1L, "Main", List.of(friend1, friend2), List.of(g), "pic");

        panel = new ComparisonPanel(mainUser);
        nameCardsForTesting();
    }

    @Test
    void testComboBoxLoadsFriends() {
        JComboBox<?> combo = findCombo(panel);
        assertNotNull(combo);

        assertEquals(2, combo.getItemCount());
        assertEquals(friend1, combo.getItemAt(0));
        assertEquals(friend2, combo.getItemAt(1));
    }

    @Test
    void testLoadFriendPanelSwitchesCard() {
        JComboBox<?> combo = findCombo(panel);
        combo.setSelectedItem(friend1);

        invokeLoad(panel);

        JButton back = findBack(panel);
        assertTrue(back.isEnabled());
        assertTrue(isStatsActive(), "stats card active");
    }

    @Test
    void testBackButtonReturnsToSelector() {
        JComboBox<?> combo = findCombo(panel);
        combo.setSelectedItem(friend2);
        invokeLoad(panel);

        JButton back = findBack(panel);
        assertTrue(back.isEnabled());

        back.doClick();

        assertFalse(back.isEnabled());
        assertTrue(isSelectorActive());
    }

    @Test
    void testLoadFriendPanelWithNull() {
        JComboBox<?> combo = findCombo(panel);
        combo.setSelectedItem(null);

        invokeLoad(panel);

        JButton back = findBack(panel);
        assertFalse(back.isEnabled());
        assertTrue(isSelectorActive());
    }

    private JComboBox<?> findCombo(Component c) {
        if (c instanceof JComboBox<?> box) return box;
        if (c instanceof Container ct) {
            for (Component child : ct.getComponents()) {
                JComboBox<?> r = findCombo(child);
                if (r != null) return r;
            }
        }
        return null;
    }

    private JButton findBack(Component c) {
        if (c instanceof JButton b && b.getText().equals("Back")) return b;
        if (c instanceof Container ct) {
            for (Component child : ct.getComponents()) {
                JButton r = findBack(child);
                if (r != null) return r;
            }
        }
        return null;
    }

    private Container findRightContainer(Component c) {
        if (c instanceof JPanel p && p.getLayout() instanceof CardLayout) return p;
        if (c instanceof Container ct) {
            for (Component child : ct.getComponents()) {
                Container r = findRightContainer(child);
                if (r != null) return r;
            }
        }
        return null;
    }

    private void nameCardsForTesting() {
        Container right = findRightContainer(panel);
        Component[] comps = right.getComponents();
        if (comps.length >= 2) {
            comps[0].setName("selector");
            comps[1].setName("stats");
        }
    }

    private boolean isSelectorActive() {
        Container right = findRightContainer(panel);
        for (Component c : right.getComponents()) {
            if ("selector".equals(c.getName()) && c.isVisible()) return true;
        }
        return false;
    }

    private boolean isStatsActive() {
        Container right = findRightContainer(panel);
        for (Component c : right.getComponents()) {
            if ("stats".equals(c.getName()) && c.isVisible()) return true;
        }
        return false;
    }

    private void invokeLoad(ComparisonPanel p) {
        try {
            var m = ComparisonPanel.class.getDeclaredMethod("loadFriendPanel");
            m.setAccessible(true);
            m.invoke(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
