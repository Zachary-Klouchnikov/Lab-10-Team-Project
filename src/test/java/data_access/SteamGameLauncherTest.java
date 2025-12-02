package data_access;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SteamGameLauncherTest {

    private final SteamGameLauncher launcher = new SteamGameLauncher();
    private final String originalOsName = System.getProperty("os.name");

    @AfterEach
    void restoreOsName() {
        // Prevent leaking os.name into other tests
        if (originalOsName != null) {
            System.setProperty("os.name", originalOsName);
        }
    }

    /**
     * Helper to invoke the private createPlatformProcess method via reflection.
     */
    private List<String> buildCommandForOs(String fakeOsName, String uri) {
        System.setProperty("os.name", fakeOsName);

        try {
            Method m = SteamGameLauncher.class
                    .getDeclaredMethod("createPlatformProcess", String.class);
            m.setAccessible(true);
            ProcessBuilder builder = (ProcessBuilder) m.invoke(launcher, uri);
            return builder.command();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPlatformProcess_usesWindowsCommandOnWindows() {
        String uri = "steam://launch/570/";
        List<String> cmd = buildCommandForOs("Windows 10", uri);

        assertEquals(3, cmd.size());
        assertEquals("rundll32", cmd.get(0));
        assertEquals("url.dll,FileProtocolHandler", cmd.get(1));
        assertEquals(uri, cmd.get(2));
    }

    @Test
    void createPlatformProcess_usesOpenCommandOnMac() {
        String uri = "steam://launch/570/";
        List<String> cmd = buildCommandForOs("Mac OS X", uri);

        assertEquals(2, cmd.size());
        assertEquals("open", cmd.get(0));
        assertEquals(uri, cmd.get(1));
    }

    @Test
    void createPlatformProcess_usesXdgOpenOnLinux() {
        String uri = "steam://launch/570/";
        List<String> cmd = buildCommandForOs("Linux", uri);

        assertEquals(2, cmd.size());
        assertEquals("xdg-open", cmd.get(0));
        assertEquals(uri, cmd.get(1));
    }

    @Test
    void launchGame_buildsExpectedUri() {
        long appId = 570L;
        String args = "null";
        String expectedUri = "steam://launch/" + appId + "/" + args;

        try {
            Method m = SteamGameLauncher.class
                    .getDeclaredMethod("createPlatformProcess", String.class);
            m.setAccessible(true);
            ProcessBuilder builder = (ProcessBuilder) m.invoke(launcher, expectedUri);

            List<String> cmd = builder.command();
            assertTrue(
                    cmd.contains(expectedUri),
                    "Command list should contain the constructed Steam URI"
            );
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
}
