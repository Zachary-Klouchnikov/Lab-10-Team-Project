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
            URL url = URI.create(String.format("https://avatars.steamstatic.com/%s.jpg", user.getImageHash())).toURL();
            BufferedImage image = ImageIO.read(url);
            Path projectRoot = getProjectRoot();
            if (projectRoot == null) {
                return;
            }
            Path imageDir = projectRoot.resolve(Paths.get("src", "main", "resources", "users"));
            File output = imageDir.resolve(String.format("%s.jpg", user.getImageHash())).toFile();

            // Do not write if exists already.
            if (!output.exists()) {
                ImageIO.write(image, "jpg", output);
            }
        } catch (Exception e) {
            // Ignore errors, rely on fallback image.
            e.printStackTrace();
            return;
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
            URL url = URI.create(String.format("http://media.steampowered.com/steamcommunity/public/images/apps/%d/%s.jpg", game.getId(), game.getImageHash())).toURL();
            BufferedImage image = ImageIO.read(url);
            Path projectRoot = getProjectRoot();
            if (projectRoot == null) {
                return;
            }
            Path imageDir = projectRoot.resolve(Paths.get("src", "main", "resources", "games"));
            File output = imageDir.resolve(String.format("%s.jpg", game.getImageHash())).toFile();

            if (!output.exists()) {
                ImageIO.write(image, "jpg", output);
            }
        } catch (Exception e) {
            // Rely on fallback.
            e.printStackTrace();
            return;
        }
    }

    /**
     * Fetches a User's profile picture from disk.
     *
     * @param user The User object.
     * @return JLabel Returns the image imbedded into a JLabel, or the default image.
     */
    public static JLabel getImage(User user) {
        JLabel out;

        Path projRoot = getProjectRoot();
        if (projRoot == null) {
            return getDefaultImage();
        }

        Path imageDir = projRoot.resolve(Paths.get("src", "main", "resources", "users"));
        File imageFile = imageDir.resolve(String.format("%s.jpg", user.getImageHash())).toFile();
        if (!imageFile.exists()) {
            return getDefaultImage();
        }

        try {
            BufferedImage image = ImageIO.read(imageFile);
            out = new JLabel(new ImageIcon(image));
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultImage();
        }
        return out;
    }

    /**
     * Fetches a Game's icon from disk.
     *
     * @param game The Game object representation.
     * @return JLabel Returns the image imbedded into a JLabel, or the default image.
     */
    public static JLabel getImage(Game game) {
        JLabel out;

        Path projRoot = getProjectRoot();
        if (projRoot == null) {
            return getDefaultImage();
        }

        Path imageDir = projRoot.resolve(Paths.get("src", "main", "resources", "games"));
        File imageFile = imageDir.resolve(String.format("%s.jpg", game.getImageHash())).toFile();
        if (!imageFile.exists()) {
            return getDefaultImage();
        }

        try {
            BufferedImage image = ImageIO.read(imageFile);
            out = new JLabel(new ImageIcon(image));
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultImage();
        }
        return out;
    }

    /**
     * Fetches the default icon from disk.
     *
     * @return JLabel Returns the default image imbedded into a JLabel, or a text label as a stand-in.
     */
    private static JLabel getDefaultImage() {
        JLabel out;
        Path projRoot = getProjectRoot();
        Path imageDir = projRoot.resolve(Paths.get("src", "main", "resources", "users"));
        File imageFile = imageDir.resolve("default.jpg").toFile();
        try {
            BufferedImage image = ImageIO.read(imageFile);
            out = new JLabel(new ImageIcon(image)); 
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to the fallback: a temp label.
            return new JLabel("[IMAGE]");
        }
        return out;
    }

    private static Path getProjectRoot() {
        try {
            return Paths.get(ImageDataAccessObject
                    .class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath())
                .getParent()
                .getParent()
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
