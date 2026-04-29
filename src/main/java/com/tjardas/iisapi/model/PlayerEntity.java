package com.tjardas.iisapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonIgnore
    private Long id;

    @Transient
    @JsonProperty("id")
    private String recordId;

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
                ", recordId='" + recordId + '\'' +
                ", name='" + name + '\'' +
                ", team='" + team + '\'' +
                ", season=" + season +
                ", points=" + points +
                '}';
    }
}
