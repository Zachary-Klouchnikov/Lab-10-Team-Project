package entity;

import java.util.List;

public class User {
    private final long steamid;
    private final String username;
    private final List<User> friends;
    private final List<Game> library;
    private final List<Game> recentGames;
    private final String profilePicture;

    public User(long id, String name, List<User> friends, List<Game> games, List<Game> recentGames, String profile) {
        this.steamid = id;
        this.username = name;
        this.friends = friends;
        this.library = games;
        this.recentGames = recentGames;
        this.profilePicture = profile;
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

    public List<Game> getRecentGames() {
        return this.recentGames;
    }

    // NOTE: Pass this as an argument to an image loader.
    public String getProfilePicture() {
        return this.profilePicture;
    }

    // Convinient string representation for debug purposes.
    @Override
    public String toString() {
        return String.format("[\n\tid: %d\n\tusername: %s\n\tpicture: %s\n]", this.steamid, this.username, this.profilePicture);
    }
}
