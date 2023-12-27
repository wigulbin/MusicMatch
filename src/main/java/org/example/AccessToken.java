package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AccessToken {
    private String accessToken = "";
    private String tokenType = "";
    private int expiresIn;


    private static final URI TOKEN_URI = URI.create("https://accounts.spotify.com/api/token");
    private AccessToken() {
    }


    private AccessToken(Map<String, String> dataJson) {
        this.accessToken = (String)dataJson.getOrDefault("access_token", "");
        this.tokenType = (String)dataJson.getOrDefault("token_type", "");
        this.expiresIn = Integer.parseInt((String)dataJson.getOrDefault("expires_in", ""));
    }


    public static AccessToken createToken(String clientID, String clientSecret) throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");
        String form = parameters.keySet().stream().map((key) -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8)).collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(TOKEN_URI).setHeader("Authorization", "Basic " + auth).setHeader("Content-Type", "application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString(form)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> dataJson = objectMapper.readValue(response.body(), new TypeReference<HashMap<String, String>>() {});
        return new AccessToken(dataJson);
    }


    public String getAccessToken() {
        return this.accessToken;
    }


    public String getTokenType() {
        return this.tokenType;
    }


    public int getExpiresIn() {
        return this.expiresIn;
    }

}
