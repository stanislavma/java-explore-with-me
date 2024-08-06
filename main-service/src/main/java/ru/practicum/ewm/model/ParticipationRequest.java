package ru.practicum.ewm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.enums.RequestState;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constants.DATE_TIME_FORMAT_PATTERN;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "participation_request")
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private RequestState status;

    @JsonFormat(pattern = DATE_TIME_FORMAT_PATTERN)
    private LocalDateTime created;

}
