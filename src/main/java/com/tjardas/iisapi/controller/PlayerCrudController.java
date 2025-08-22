package com.tjardas.iisapi.controller;

import com.tjardas.iisapi.model.PlayerEntity;
import com.tjardas.iisapi.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerCrudController {

    private final PlayerRepository playerRepository;

    @GetMapping
    public List<PlayerEntity> getAll() {
        return playerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerEntity> getById(@PathVariable Long id) {
        return playerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public PlayerEntity create(@RequestBody PlayerEntity player) {
        return playerRepository.save(player);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerEntity> update(@PathVariable Long id, @RequestBody PlayerEntity player) {
        return playerRepository.findById(id)
                .map(existing -> {
                    player.setId(existing.getId());
                    return ResponseEntity.ok(playerRepository.save(player));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
