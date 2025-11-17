package entity;

public class Game {
    private final long appid;
    private final String title;
    private final String thumbnail;
    private final long playtime;

    public Game(long appid, String title, String thumbnail, long playtime) {
        this.appid = appid;
        this.title = title;
        this.thumbnail = thumbnail;
        this.playtime = playtime;
    }

    public long getId() {
        return this.appid;
    }

    public String getTitle() {
        return this.title;
    }
    
    // NOTE: Reminder to pass this as an arguemnt to some image reader.
    public String getThumbnail() {
        return this.thumbnail;
    }

    // NOTE: Returns playtime in seconds.
    public long getPlaytime() {
        return this.playtime * 60;
    }
    
    // Convinient string representation for debug purposes.
    @Override
    public String toString() {
        return String.format("[\n\tappid: %d\n\ttitle: %s\n\tthumbnail: %s\n\tplaytime(minutes): %d\n]", this.appid, this.title, this.thumbnail, this.playtime);
    }
}
