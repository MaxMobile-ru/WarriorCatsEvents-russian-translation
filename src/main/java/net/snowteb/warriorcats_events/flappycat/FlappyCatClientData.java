package net.snowteb.warriorcats_events.flappycat;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.player.LocalPlayer;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.wcedatabase.DataManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FlappyCatClientData {
    private static int clientScore = 0;
    private static Map<String, Integer> globalScores = new HashMap<>();
    private static Map<String, Integer> top10Scores;

    public static int getClientScore() {
        return clientScore;
    }

    public static boolean trySetClientScore(int i) {
        if (i > clientScore) {
            clientScore = i;
            return true;
        }
        return false;
    }

    public static void uploadScore() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            try {
                if (verifyOfficialAccount().get()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("score", getClientScore());
                    obj.addProperty("name", player.getName().getString());

                    DataManager.uploadData(obj, DataManager.URL_FLAPPY_CAT);
                } else {
                    WarriorCatsEvents.LOGGER.error("Couldn't verify Minecraft profile.");
                }
            } catch (Exception e) {
                WarriorCatsEvents.LOGGER.error("Error while trying to upload Flappy Cat score: ", e);
            }
        }
    }


    public static Map<String, Integer> getGlobalScores() {
        return globalScores;
    }

    public static void setScores(Map<String, Integer> globalScores) {
        FlappyCatClientData.globalScores = globalScores;
        String clientName = Minecraft.getInstance().getUser().getName();
         if (globalScores.get(clientName) != null) {
             trySetClientScore(globalScores.get(clientName));
         }
    }

    public static Map<String, Integer> getTop10Scores() {
        if (top10Scores == null) {
            top10Scores = getGlobalScores().entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10).collect(Collectors.toMap(
                            Map.Entry::getKey, Map.Entry::getValue,
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));
        }

        return top10Scores;
    }

    public static CompletableFuture<Boolean> verifyOfficialAccount() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = Minecraft.getInstance().getUser();
                String accessToken = user.getAccessToken();

                if (accessToken.isEmpty()) {
                    return false;
                }

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.minecraftservices.com/minecraft/profile"))
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                return response.statusCode() == 200;

            } catch (Exception e) {
                return false;
            }
        });
    }


}
