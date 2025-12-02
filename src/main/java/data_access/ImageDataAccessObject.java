package data_access;

import entity.Game;
import entity.User;

import java.net.URL;
import java.net.URI;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;

// TODO: Abstract the methods to be in interfaces.
// More specifically: Get a User profile interface, Game image interface.
public class ImageDataAccessObject {

    /**
     * Downloads a user's profile picture off the internet.
     *
     * @param User The User object.
     * @return void Downloads an image and stores it in a pre-determined location.
     */
    public static void downloadImage(User user) {
        try {
            Path projectRoot = getProjectRoot();
            if (projectRoot == null) {
                return;
            }
            Path imageDir = projectRoot.resolve(Paths.get("src", "main", "resources", "users"));
            File output = imageDir.resolve(String.format("%s.jpg", user.getImageHash())).toFile();

            // Skip download if already cached
            if (output.exists()) {
                return;
            }

            URL url = URI.create(String.format("https://avatars.steamstatic.com/%s.jpg", user.getImageHash())).toURL();
            BufferedImage image = ImageIO.read(url);
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            // Ignore errors, rely on fallback image.
            e.printStackTrace();
        }
    }

    /**
     * Downloads a game's icon off the internet.
     *
     * @param game The Game object representation.
     * @return void Downloads an image and stores it in a pre-determined location.
     */
    public static void downloadImage(Game game) {
        try {
            Path projectRoot = getProjectRoot();
            if (projectRoot == null) {
                return;
            }
            Path imageDir = projectRoot.resolve(Paths.get("src", "main", "resources", "games"));
            File output = imageDir.resolve(String.format("%s.jpg", game.getImageHash())).toFile();

            // Skip download if already cached
            if (output.exists()) {
                return;
            }

            URL url = URI
                    .create(String.format("http://media.steampowered.com/steamcommunity/public/images/apps/%d/%s.jpg",
                            game.getId(), game.getImageHash()))
                    .toURL();
            BufferedImage image = ImageIO.read(url);
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            // Rely on fallback.
            e.printStackTrace();
        }
    }

    /**
     * Fetches a User's profile picture from disk.
     *
     * @param user The User object.
     * @return Icon Returns the image imbedded into a JLabel, or the default image.
     */
    public static Icon getImage(User user) {
        ImageIcon out;

        Path projRoot = getProjectRoot();
        if (projRoot == null) {
            return getDefaultImage().getIcon();
        }

        Path imageDir = projRoot.resolve(Paths.get("src", "main", "resources", "users"));
        File imageFile = imageDir.resolve(String.format("%s.jpg", user.getImageHash())).toFile();
        if (!imageFile.exists()) {
            return getDefaultImage().getIcon();
        }

        try {
            BufferedImage image = ImageIO.read(imageFile);
            out = new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultImage().getIcon();
        }
        return out;
    }

    /**
     * Fetches a Game's icon from disk.
     *
     * @param game The Game object representation.
     * @return ImageIcon Returns the image or the default image.
     */
    public static Icon getImage(Game game) {
        ImageIcon out;

        Path projRoot = getProjectRoot();
        if (projRoot == null) {
            return getDefaultImage().getIcon();
        }

        Path imageDir = projRoot.resolve(Paths.get("src", "main", "resources", "games"));
        File imageFile = imageDir.resolve(String.format("%s.jpg", game.getImageHash())).toFile();
        if (!imageFile.exists()) {
            return getDefaultImage().getIcon();
        }

        try {
            BufferedImage image = ImageIO.read(imageFile);
            out = new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultImage().getIcon();
        }
        return out;
    }

    /**
     * Fetches the default icon from disk.
     *
     * @return JLabel Returns the default image imbedded into a JLabel, or a text
     *         label as a stand-in.
     */
    public static JLabel getDefaultImage() {
        Path projRoot = getProjectRoot();
        if (projRoot != null) {
            Path imageDir = projRoot.resolve(Paths.get("src", "main", "resources"));
            File imageFile = imageDir.resolve("default.jpg").toFile();
            try {
                BufferedImage image = ImageIO.read(imageFile);
                return new JLabel(new ImageIcon(image));
            } catch (IOException e) {
                // Fall through to placeholder
            }
        }
        // Return a placeholder label instead of null
        JLabel placeholder = new JLabel("[No Image]");
        placeholder.setPreferredSize(new java.awt.Dimension(64, 64));
        return placeholder;
    }

    private static Path getProjectRoot() {
        try {
            return Paths.get(ImageDataAccessObject.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .normalize())
                    .getParent()
                    .getParent();
        } catch (Exception e) {
            // How tf did this fail?
            e.printStackTrace();
            // Fallback: Get runtime dir.
            return Paths.get(System.getProperty("user.dir"));
        }
    }
}
