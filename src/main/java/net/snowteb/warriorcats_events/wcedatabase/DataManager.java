package net.snowteb.warriorcats_events.wcedatabase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;

public class DataManager {

    private static final String KEY = "sb_publishable_z_Qwl9VFroFDOfK6hQIUZA_XZiVR7lK";
    public static final String URL_FLAPPY_CAT = "https://fmcrsmxmxsrzmvhrozfo.supabase.co/rest/v1/flappy-cat-scores";
    public static final String URL_CONTRIBUTORS = "https://fmcrsmxmxsrzmvhrozfo.supabase.co/rest/v1/contributors";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void uploadData(JsonObject json, String url) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", KEY)
                .header("Authorization", "Bearer " + KEY)
                .header("Content-Type", "application/json")
                .header("Prefer", "resolution=merge-duplicates")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        WarriorCatsEvents.LOGGER.info("Data uploaded successfully");
                    } else {
                        WarriorCatsEvents.LOGGER.error("Data couldn't be uploaded. {} - {}",
                                response.statusCode(), response.body());
                    }
                })
                .exceptionally(ex -> {
                    WarriorCatsEvents.LOGGER.error("Data couldn't be uploaded. {} - {}", ex.getMessage(), ex.getCause());

                    return null;
                });
    }

    public static void getScores(Consumer<Map<String, Integer>> result) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_FLAPPY_CAT + "?select=name,score"))
                .header("apikey", KEY)
                .header("Authorization", "Bearer " + KEY)
                .GET()
                .build();

        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        WarriorCatsEvents.LOGGER.error("Scores couldn't be obtained. {} - {}",
                                response.statusCode(), response.body());
                        result.accept(new LinkedHashMap<>());
                        return;
                    }

                    Map<String, Integer> scores = new LinkedHashMap<>();
                    JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();

                    for (int i = 0; i < array.size(); i++) {
                        JsonObject obj = array.get(i).getAsJsonObject();
                        String name = obj.get("name").getAsString();
                        int score = obj.get("score").getAsInt();
                        scores.put(name, score);
                    }
                    result.accept(scores);

                })
                .exceptionally(ex -> {
                    WarriorCatsEvents.LOGGER.error("Data couldn't be obtained. {} - {}",
                            ex.getMessage(), ex.getCause());
                    result.accept(new LinkedHashMap<>());
                    return null;
                });
    }

    public static void getContributors(Consumer<Set<UUID>> result) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_CONTRIBUTORS + "?select=UUID"))
                .header("apikey", KEY)
                .header("Authorization", "Bearer " + KEY)
                .GET()
                .build();

        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        WarriorCatsEvents.LOGGER.error("Data couldn't be obtained. {} - {}", response.statusCode(), response.body());
                        result.accept(new HashSet<>());
                        return;
                    }

                    Set<UUID> uuids = new HashSet<>();
                    JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();

                    for (int i = 0; i < array.size(); i++) {
                        JsonObject obj = array.get(i).getAsJsonObject();
                        String uuid = obj.get("UUID").getAsString();
                        uuids.add(UUID.fromString(uuid));
                    }
                    result.accept(uuids);
                    WarriorCatsEvents.LOGGER.info("Contributors data obtained.");

                })
                .exceptionally(ex -> {
                    WarriorCatsEvents.LOGGER.error("Data couldn't be obtained. {} - {}",
                            ex.getMessage(), ex.getCause());
                    result.accept(new HashSet<>());
                    return null;
                });
    }

}