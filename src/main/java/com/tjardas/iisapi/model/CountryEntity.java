package com.tjardas.iisapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "country_entity")
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment or identity column    private Long id;
    private Long id;

    @Column(name = "country_name")
    private String name;

    @Column(name = "country_sub_region")
    private String subRegion;

    @Column(name = "country_year")
    private Integer year;

    @Column(name = "country_value")
    private Float value;

    @Override
    public String toString() {
        return "CountryEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subRegion='" + subRegion + '\'' +
                ", year=" + year +
                ", value=" + value +
                '}';
    }

}
