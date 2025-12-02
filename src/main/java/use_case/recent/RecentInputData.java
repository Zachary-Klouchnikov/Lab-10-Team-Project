package use_case.recent;

public class RecentInputData {
    private final long steamid;

    public RecentInputData(Long id) {
        this.steamid = id;
    }

    public Long getId() {
        return this.steamid;
    }
}
