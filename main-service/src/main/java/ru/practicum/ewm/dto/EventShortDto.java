package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constants.DATE_TIME_FORMAT_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto {

    private Long id;

    private String title;

    private String annotation;

    private CategoryDto category;

    @JsonFormat(pattern = DATE_TIME_FORMAT_PATTERN)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Boolean paid;

    private Long views;

    private Long confirmedRequests;

}