package com.tjardas.iisapi.controller;

import com.tjardas.iisapi.model.PlayerEntity;
import com.tjardas.iisapi.service.NbaApiService;
import lombok.RequiredArgsConstructor;
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
    public PlayerEntity create(@RequestBody PlayerEntity player) {
        return nbaApiService.createPlayer(player);
    }

    @PatchMapping("/{recordId}")
    public ResponseEntity<PlayerEntity> update(@PathVariable String recordId, @RequestBody PlayerEntity player) {
        return ResponseEntity.ok(nbaApiService.updatePlayer(recordId, player));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> delete(@PathVariable String recordId) {
        nbaApiService.deletePlayer(recordId);
        return ResponseEntity.noContent().build();
    }
}
