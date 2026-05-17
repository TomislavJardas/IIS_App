package com.tjardas.iisapi.controller;

import com.tjardas.iisapi.dto.PlayerRequest;
import com.tjardas.iisapi.model.PlayerEntity;
import com.tjardas.iisapi.service.NbaApiService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerCrudController {

    private final NbaApiService nbaApiService;

    @GetMapping
    public List<PlayerEntity> getAll() {
        return nbaApiService.getAllPlayers();
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<PlayerEntity> getById(@PathVariable String recordId) {
        return ResponseEntity.ok(nbaApiService.getPlayerById(recordId));
    }

    @PostMapping
    public PlayerEntity create(@Valid @RequestBody PlayerRequest playerRequest) {
        return nbaApiService.createPlayer(toEntity(playerRequest));
    }

    @PatchMapping("/{recordId}")
    public ResponseEntity<PlayerEntity> update(@PathVariable String recordId, @Valid @RequestBody PlayerRequest playerRequest) {
        return ResponseEntity.ok(nbaApiService.updatePlayer(recordId, toEntity(playerRequest)));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> delete(@PathVariable String recordId) {
        nbaApiService.deletePlayer(recordId);
        return ResponseEntity.noContent().build();
    }

    private PlayerEntity toEntity(PlayerRequest playerRequest) {
        return PlayerEntity.builder()
                .name(playerRequest.getName())
                .team(playerRequest.getTeam())
                .season(playerRequest.getSeason())
                .points(playerRequest.getPoints())
                .build();
    }
}
