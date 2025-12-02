package entity;

import data_access.ImageDataAccessObject;
import javax.swing.Icon;

public class Game {
    private final long appid;
    private final String name;
    private final int playtime;
    private final String thumbnail;
    private final int recentPlaytime;

    public Game(long appid, String title, int playtime_forever, String thumbnail, int recent_playtime) {
        this.appid = appid;
        this.name = title;
        this.playtime = playtime_forever;
        this.thumbnail = thumbnail;
        this.recentPlaytime = recent_playtime;
    }

    public long getId() {
        return this.appid;
    }

    public String getTitle() {
        return this.name;
    }

    // Get Playtime in minutes.
    public int getPlaytime() {
        return this.playtime;
    }

    public int getRecentPlaytime() {
        return this.recentPlaytime;
    }

    // This should return a string, when fed into an ImageDataAccessObject, actually returns an image.
    public String getImageHash() {
        return this.thumbnail;
    }

    public Icon getImage() {
        return ImageDataAccessObject.getImage(this);
    }

    @Override
    public String toString() {
        return String.format("Game: [\n\tid: %d\n\ttitle: %s\n\tplaytime(minutes): %d\n\tthumbnail: %s\n\t recent playtime(min): %s\n]", this.appid, this.name, this.playtime, this.recentPlaytime);
    }
}
