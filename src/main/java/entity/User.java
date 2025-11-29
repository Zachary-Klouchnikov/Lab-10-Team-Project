package entity;

import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

import data_access.ImageDataAccessObject;

import javax.swing.JLabel;

public class User {
    private final long steamid;
    private final String username;
    private final List<User> friends;
    private final List<Game> library;
    private final List<Game> recentGames;
    private final String profilePicture;
    private final List<Path> libraryPaths;

    public User(long id, String name, List<User> friends, List<Game> games, String profile, List<Path> libraryPaths) {
        this.steamid = id;
        this.username = name;
        this.friends = friends;
        this.library = games;
        this.libraryPaths = libraryPaths;
        this.profilePicture = profile;

        this.recentGames = new ArrayList<>();
        for (int i = 0; i < games.size(); ++i) {
            if (games.get(i).getRecentPlaytime() == 0)
                this.recentGames.add(games.get(i));
        }
    }

    public long getId() {
        return this.steamid;
    }

    public String getUsername() {
        return this.username;
    }

    public List<User> getFriends() {
        return this.friends;
    }

    public List<Game> getLibrary() {
        return this.library;
    }

    public List<Path> getLibraryPaths() {
        return this.libraryPaths;
    }

    public List<Game> getRecentGames() {
        return this.recentGames;
    }

    public String getImageHash() {
        return this.profilePicture;
    }
    
    public JLabel getImage() {
        return ImageDataAccessObject.getImage(this);
    }

    // Convinient string representation for debug purposes.
    @Override
    public String toString() {
        return String.format("User: [\n\tid: %d\n\tusername: %s\n\tpicture: %s\n]", this.steamid, this.username, this.profilePicture);
    }
}
