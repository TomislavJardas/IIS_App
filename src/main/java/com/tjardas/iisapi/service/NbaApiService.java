package com.tjardas.iisapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tjardas.iisapi.model.PlayerEntity;
import com.tjardas.iisapi.xml.Players;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NbaApiService {

    private static final String PLAYERS_COLLECTION = "players";

    @Value("${pocketbase.instance}")
    private String pocketBaseInstance;

    @Value("${pocketbase.auth-token:}")
    private String pocketBaseAuthToken;

    private final RestTemplate restTemplate = buildRestTemplate();

    public Players getFilteredPlayers(String name, String team, Integer season) {
        String endpoint = String.format("%s/api/collections/%s/records", pocketBaseInstance, PLAYERS_COLLECTION);
        HttpEntity<Void> requestEntity = new HttpEntity<>(buildHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, JsonNode.class);

        List<Players.Player> filtered = new ArrayList<>();
        JsonNode items = response.getBody() == null ? null : response.getBody().path("items");

        if (items != null && items.isArray()) {
            for (JsonNode node : items) {
                Players.Player player = toXmlPlayer(node);
                if ((name == null || player.getName().equalsIgnoreCase(name))
                        && (team == null || player.getTeam().equalsIgnoreCase(team))
                        && (season == null || player.getSeason() == season)) {
                    filtered.add(player);
                }
            }
        }

        Players players = new Players();
        players.setPlayers(filtered);
        return players;
    }

    public List<PlayerEntity> getAllPlayers() {
        String endpoint = String.format("%s/api/collections/%s/records", pocketBaseInstance, PLAYERS_COLLECTION);
        HttpEntity<Void> requestEntity = new HttpEntity<>(buildHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, JsonNode.class);

        List<PlayerEntity> players = new ArrayList<>();
        JsonNode items = response.getBody() == null ? null : response.getBody().path("items");

        if (items != null && items.isArray()) {
            for (JsonNode item : items) {
                players.add(toEntity(item));
            }
        }

        return players;
    }

    public PlayerEntity getPlayerById(String recordId) {
        String endpoint = String.format("%s/api/collections/%s/records/%s", pocketBaseInstance, PLAYERS_COLLECTION, recordId);
        HttpEntity<Void> requestEntity = new HttpEntity<>(buildHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, JsonNode.class);
        return toEntity(response.getBody());
    }

    public PlayerEntity createPlayer(PlayerEntity player) {
        String endpoint = String.format("%s/api/collections/%s/records", pocketBaseInstance, PLAYERS_COLLECTION);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(toPocketBasePayload(player), buildHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, JsonNode.class);
        return toEntity(response.getBody());
    }

    public PlayerEntity updatePlayer(String recordId, PlayerEntity player) {
        String endpoint = String.format("%s/api/collections/%s/records/%s", pocketBaseInstance, PLAYERS_COLLECTION, recordId);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(toPocketBasePayload(player), buildHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.PATCH, requestEntity, JsonNode.class);
        return toEntity(response.getBody());
    }

    public void deletePlayer(String recordId) {
        String endpoint = String.format("%s/api/collections/%s/records/%s", pocketBaseInstance, PLAYERS_COLLECTION, recordId);
        HttpEntity<Void> requestEntity = new HttpEntity<>(buildHeaders());
        restTemplate.exchange(endpoint, HttpMethod.DELETE, requestEntity, Void.class);
    }

    private RestTemplate buildRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(15000);
        return new RestTemplate(requestFactory);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (pocketBaseAuthToken != null && !pocketBaseAuthToken.isBlank()) {
            headers.setBearerAuth(pocketBaseAuthToken);
        }
        return headers;
    }

    private Map<String, Object> toPocketBasePayload(PlayerEntity player) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", player.getName());
        payload.put("team", player.getTeam());
        payload.put("season", player.getSeason());
        payload.put("points", player.getPoints());
        return payload;
    }

    private Players.Player toXmlPlayer(JsonNode node) {
        Players.Player player = new Players.Player();
        player.setName(node.path("name").asText(""));
        player.setTeam(node.path("team").asText(""));
        player.setSeason(node.path("season").asInt(0));
        player.setPoints((float) node.path("points").asDouble(0));
        return player;
    }

    private PlayerEntity toEntity(JsonNode node) {
        PlayerEntity player = new PlayerEntity();
        if (node == null) {
            return player;
        }

        String rawId = node.path("id").asText("");
        if (!rawId.isBlank()) {
            player.setRecordId(rawId);
        }

        player.setName(node.path("name").asText(""));
        player.setTeam(node.path("team").asText(""));
        player.setSeason(node.path("season").asInt(0));
        player.setPoints((float) node.path("points").asDouble(0));
        return player;
    }
}
