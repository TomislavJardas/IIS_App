package com.tjardas.iisapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerRequest {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Team is required.")
    private String team;

    @NotNull(message = "Season is required.")
    @Min(value = 1, message = "Season must be greater than 0.")
    private Integer season;

    @NotNull(message = "Points is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Points must be greater than or equal to 0.")
    private Float points;
}
