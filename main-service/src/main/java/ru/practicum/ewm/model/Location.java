package ru.practicum.ewm.model;

import lombok.*;
import ru.practicum.ewm.dto.location.LocationDto;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lon", nullable = false)
    private Double lon;

    public Location(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Location(LocationDto location) {
        this.lat = location.getLat();
        this.lon = location.getLon();
    }

}
