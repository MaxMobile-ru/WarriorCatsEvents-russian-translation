package net.snowteb.warriorcats_events.event;

import net.snowteb.warriorcats_events.WarriorCatsEvents;
import org.apache.commons.io.IOUtils;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateCheck {

    public static boolean updateAvailable = false;
    public static String latestVersion = "Unknown";
    public static String update_message = "";

    private static final String VERSION_URL = "https://raw.githubusercontent.com/Snowo69/WarriorCatsEvents/1.21.1/latest_version";
    private static final String UPDATE_MSG_URL = "https://raw.githubusercontent.com/Snowo69/WarriorCatsEvents/1.21.1/updt_msg";

    public static void checkForUpdates() {
        WarriorCatsEvents.LOGGER.info("[Warrior Cats Events] Checking updates...");

        new Thread(() -> {
            try {
                String version = IOUtils.toString(new URL(VERSION_URL), StandardCharsets.UTF_8).trim();
                update_message = IOUtils.toString(new URL(UPDATE_MSG_URL), StandardCharsets.UTF_8).trim();

                latestVersion = version;

                String current = WarriorCatsEvents.MOD_VERSION.trim();

                if (!current.equalsIgnoreCase(version)) {
                    updateAvailable = true;
                    WarriorCatsEvents.LOGGER.warn("[Warrior Cats Events] New version available: " + version);
                } else {
                    WarriorCatsEvents.LOGGER.info("[Warrior Cats Events] Is up to date");
                }
            } catch (Exception e) {
                WarriorCatsEvents.LOGGER.error("[Warrior Cats Events] Couldn't verify version: {}", e.getMessage());
            }
        }).start();
    }
}




