package data_access;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryDataAccessObject {

    private final Path vdfPath =
            Paths.get("C:/Program Files (x86)/Steam/steamapps/libraryfolders.vdf");

    private static final Pattern PATH_PATTERN =
            Pattern.compile("\"path\"\\s*\"([^\"]+)\"");

    public List<Path> loadLibraryFolders() {
        if (!Files.exists(vdfPath)) {
            System.err.println("libraryfolders.vdf not found.");
            return List.of();
        }

        List<Path> result = new ArrayList<>();

        try (Scanner scanner = new Scanner(vdfPath, "UTF-8")) {
            while (scanner.hasNextLine()) {
                Matcher matcher = PATH_PATTERN.matcher(scanner.nextLine());
                if (matcher.find()) {
                    result.add(Paths.get(matcher.group(1)));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to read Steam library folders: " + e.getMessage());
        }

        return result;
    }
}
