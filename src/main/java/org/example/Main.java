package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static final URI TOKEN_URI = URI.create("https://accounts.spotify.com/api/token");
    public static String CLIENT_ID = "";
    public static String CLIENT_SECRET = "";


    public static void main(String[] args) throws IOException, InterruptedException {
        try{
            SpotifyClient client = new SpotifyClient();
            Track track = client.findTrack("11dFghVXANMlKmJXsNCbNl");
            System.out.println(track);
        } catch (Exception e){}
    }


    public static Map<String, Object> parseJsonStringForMap(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> myMap = objectMapper.readValue(json, new TypeReference<HashMap<String,Object>>() {});
        System.out.println(myMap);
        return myMap;
    }
    public static String getResourcePath(String resourceName) {
        URL resource = Main.class.getClassLoader().getResource(resourceName);
        if(resource != null) {
            String path = resource.getPath();
            if(path.startsWith("/"))
                path = path.substring(1);
            return path;
        }

        return "";
    }







    public static String callSpotifyNoLibrary() throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");
        String form = parameters.keySet().stream()
                .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(TOKEN_URI)
                .setHeader("Authorization", "Basic " + auth)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> myMap = objectMapper.readValue(response.body(), new TypeReference<HashMap<String,String>>() {});
        return myMap.getOrDefault("access_token", "");
    }

    public static String callSpotifyJAXRS() throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
        try(Client client = ClientBuilder.newClient();)
        {
            WebTarget target = client.target(TOKEN_URI);

            Form form = new Form();
            form.param("grant_type", "client_credentials");

            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
            invocationBuilder.header("Authorization", "Basic " + auth);
            String accessTokenJson;
            try (Response response = invocationBuilder.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE))) {
                accessTokenJson = response.readEntity(String.class);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> myMap = objectMapper.readValue(accessTokenJson, new TypeReference<HashMap<String,String>>() {});
            return myMap.getOrDefault("access_token", "");
        }
    }
}