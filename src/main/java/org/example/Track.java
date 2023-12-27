package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Track {
    private String id = "";
    private String name = "";
    private int popularity;
    private String previewUrl = "";

    public Track(Map<String, Object> dataMap) {
        this.id = dataMap.getOrDefault("id", "").toString();
        this.name = dataMap.getOrDefault("name", "").toString();
        this.popularity = Integer.parseInt(dataMap.getOrDefault("popularity", "0").toString());
        Object var3 = dataMap.get("preview_url");
        if (var3 instanceof String previewString) {
            this.previewUrl = previewString;
        }

    }

    public static Track findTrack(String trackID, AccessToken ourToken) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder().uri(new URI("https://api.spotify.com/v1/tracks/" + trackID)).header("Authorization", "Bearer " + ourToken.getAccessToken()).GET().build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse.body());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> dataJson = objectMapper.readValue(postResponse.body(), new TypeReference<HashMap<String, Object>>() {});
        return new Track(dataJson);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPopularity() {
        return this.popularity;
    }

    public String getPreviewUrl() {
        return this.previewUrl;
    }

}
