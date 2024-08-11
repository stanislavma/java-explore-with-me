package ru.practicum.ewm.stats.model;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "endpoint_hit", schema = "public")
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app", nullable = false)
    private String app;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime hitDate;

    @Transient
    private Long hits;

    public EndpointHit(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hitDate = null;
        this.ip = null;
        this.hits = hits;
    }

}
