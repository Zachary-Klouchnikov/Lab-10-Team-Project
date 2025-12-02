package use_case.auth;

import java.util.List;
import javax.swing.JLabel;

public class AuthOutputData {
    private final Long id;
    private final String username;
    private final JLabel profilePicture;
    private final List<JLabel> gameLabels;
    private final List<JLabel> friendLabels;

    public AuthOutputData(Long id, String name, JLabel profilePic, List<JLabel> game, List<JLabel> friend) {
        this.id = id;
        this.username = name;
        this.profilePicture = profilePic;
        this.gameLabels = game;
        this.friendLabels = friend;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.username;
    }

    public JLabel getProfilePicture() {
        return this.profilePicture;
    }

    public List<JLabel> getGameLabels() {
        return this.gameLabels;
    }

    public List<JLabel> getFriendLabels() {
        return this.friendLabels;
    }

}
