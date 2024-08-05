package ru.practicum.ewm.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.common.Constants.DATE_TIME_FORMAT_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchCriteriaDto {

    private String text;
    private List<Long> categories;
    private Boolean paid;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN)
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN)
    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable = false;
    private String sort = "VIEWS";
    private int from = 0;
    private int size = 10;

}