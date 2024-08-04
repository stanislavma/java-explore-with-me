package ru.practicum.ewm.model;

import lombok.*;
import ru.practicum.ewm.dto.LocationDto;

import javax.persistence.*;

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

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    public Location(Double lat, Double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public Location(LocationDto location) {
        this.latitude = location.getLat();
        this.longitude = location.getLon();
    }

}
