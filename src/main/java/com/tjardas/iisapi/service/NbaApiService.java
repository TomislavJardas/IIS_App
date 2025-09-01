package com.tjardas.iisapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjardas.iisapi.xml.Players;
import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NbaApiService {
    private static final String API_URL = "https://api-nba-v1.p.rapidapi.com/players/statistics?game=8133";
    private static final String API_HOST = "api-nba-v1.p.rapidapi.com";
    private static final String DEFAULT_API_KEY = "1878146bcdmsh2932967a89b2b3ap1e33a9jsn34f5e618dc13";

    private final List<Players.Player> records = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void loadApiData() {
        String apiKey = System.getenv("RAPIDAPI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = DEFAULT_API_KEY;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", apiKey);
            headers.set("X-RapidAPI-Host", API_HOST);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL, HttpMethod.GET, requestEntity, String.class);

            if (response.getBody() != null) {
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode dataArray = root.path("response");
                if (dataArray.isArray()) {
                    for (JsonNode node : dataArray) {
                        Players.Player p = new Players.Player();
                        String first = node.path("player").path("firstname").asText("");
                        String last = node.path("player").path("lastname").asText("");
                        p.setName((first + " " + last).trim());
                        p.setTeam(node.path("team").path("name").asText(""));
                        p.setSeason(node.path("game").path("season").asInt(0));
                        p.setPoints((float) node.path("points").asDouble(0));
                        records.add(p);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching data from API", e);
        }
    }

    public Players getFilteredPlayers(String name, String team, Integer season) {
        List<Players.Player> filtered = records.stream()
                .filter(r -> name == null || r.getName().equalsIgnoreCase(name))
                .filter(r -> team == null || r.getTeam().equalsIgnoreCase(team))
                .filter(r -> season == null || r.getSeason() == season)
                .toList();

        Players players = new Players();
        players.setPlayers(filtered);
        return players;
    }
}
