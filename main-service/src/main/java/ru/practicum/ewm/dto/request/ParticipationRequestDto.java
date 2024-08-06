package ru.practicum.ewm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.enums.RequestState;

import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constants.DATE_TIME_FORMAT_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {

    private Long id;

    private Long event;

    private Long requester;

    private RequestState status;

    @JsonFormat(pattern = DATE_TIME_FORMAT_PATTERN)
    private LocalDateTime created;

}