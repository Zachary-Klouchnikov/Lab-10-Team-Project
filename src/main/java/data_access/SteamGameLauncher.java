package data_access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SteamGameLauncher {

    /**
     * Launches a Steam game with launch arguments.
     *
     * @param appId The Steam AppID (e.g., 570 for Dota2)
     * @param args  Launch arguments (e.g., "-novid -fullscreen")
     * @return true if the command executed successfully, false otherwise
     */
    public boolean launchGame(long appId, String args) {
        String uri = "steam://launch/" + appId + "/" + args;

        try {
            ProcessBuilder builder = createPlatformProcess(uri);
            builder.start();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a ProcessBuilder with the correct command per OS.
     */
    private ProcessBuilder createPlatformProcess(String uri) {
        String os = System.getProperty("os.name").toLowerCase();

        List<String> cmd = new ArrayList<>();

        if (os.contains("win")) {
            // Windows: use Windows URL handler
            cmd.add("rundll32");
            cmd.add("url.dll,FileProtocolHandler");
            cmd.add(uri);

        } else if (os.contains("mac")) {
            // macOS: use 'open' command
            cmd.add("open");
            cmd.add(uri);

        } else {
            // Linux: use xdg-open
            cmd.add("xdg-open");
            cmd.add(uri);
        }

        return new ProcessBuilder(cmd);
    }
}
