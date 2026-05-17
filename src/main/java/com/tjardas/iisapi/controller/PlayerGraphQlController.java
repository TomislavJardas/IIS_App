package com.tjardas.iisapi.controller;

import com.tjardas.iisapi.model.PlayerEntity;
import com.tjardas.iisapi.service.NbaApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlayerGraphQlController {

    private final NbaApiService nbaApiService;

    @QueryMapping
    public List<PlayerEntity> players() {
        return nbaApiService.getAllPlayers();
    }

    @QueryMapping
    public PlayerEntity playerById(@Argument String recordId) {
        return nbaApiService.getPlayerById(recordId);
    }

    @MutationMapping
    public PlayerEntity createPlayer(@Argument PlayerInput input) {
        return nbaApiService.createPlayer(toEntity(input));
    }

    @MutationMapping
    public PlayerEntity updatePlayer(@Argument String recordId, @Argument PlayerInput input) {
        return nbaApiService.updatePlayer(recordId, toEntity(input));
    }

    @MutationMapping
    public Boolean deletePlayer(@Argument String recordId) {
        nbaApiService.deletePlayer(recordId);
        return true;
    }

    private PlayerEntity toEntity(PlayerInput input) {
        return PlayerEntity.builder()
                .name(input.name())
                .team(input.team())
                .season(input.season())
                .points(input.points())
                .build();
    }

    public record PlayerInput(String name, String team, Integer season, Float points) {
    }
}
