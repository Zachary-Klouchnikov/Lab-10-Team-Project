package interface_adapter.loggedin;

import entity.User;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JLabel;

public class LoggedinState {
    private User user = null;
    private Long steamId = null;
    private String username = "";
    private JLabel profilePicture = new JLabel();
    private List<JLabel> gameLabels = new ArrayList<>();
    private List<JLabel> userLabels = new ArrayList<>();
    private List<JLabel> recentLabels = new ArrayList<>();
    private String errorMessage = ""; 
    private List<User> friends = new ArrayList<>();

    public String getError() {
        return this.errorMessage;
    }

    public void setError(String errMsg) {
        this.errorMessage = errMsg;
    }

    public Long getId() {
        return this.steamId;
    }

    public void setId(Long id) {
        this.steamId = id;
    }

    public String getName() {
        return this.username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public JLabel getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(JLabel label) {
        this.profilePicture = label;
    }

    public List<JLabel> getGameLabels() {
        return this.gameLabels;
    }

    public void setGameLabels(List<JLabel> list) {
        this.gameLabels = list;
    }

    public List<JLabel> getFriendLabels() {
        return this.userLabels;
    }

    public void setFriendLabels(List<JLabel> list) {
        this.userLabels = list;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getFriends() {
        return this.friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<JLabel> getRecent() {
        return this.recentLabels;
    }

    public void setRecent(List<JLabel> recent) {
        this.recentLabels = recent;
    }
}
