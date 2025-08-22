package com.tjardas.iisapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_entity")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_name")
    private String name;

    @Column(name = "player_team")
    private String team;

    @Column(name = "player_season")
    private Integer season;

    @Column(name = "player_points")
    private Float points;

    @Override
    public String toString() {
        return "PlayerEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", team='" + team + '\'' +
                ", season=" + season +
                ", points=" + points +
                '}';
    }
}
