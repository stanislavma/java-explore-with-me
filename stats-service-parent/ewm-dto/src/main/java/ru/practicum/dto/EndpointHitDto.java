package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {

    private Long id;

    @NotNull(message = "Сервис не должен быть null")
    @NotBlank(message = "Сервис не должен быть пустым")
    private String app;

    @NotNull(message = "URI не должно быть null")
    @NotBlank(message = "URI не должно быть пустым")
    private String uri;

    @NotNull(message = "IP адрес не должен быть null")
    @NotBlank(message = "IP адрес не должен быть пустым")
    @Pattern(regexp = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$",
            message = "IP адрес должен быть правильного формата")
    private String ip;

    @NotNull(message = "Временная метка не должна быть null")
    @NotBlank(message = "Временная метка не должна быть пустой")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
            message = "Дата должна быть в формате yyyy-MM-dd HH:mm:ss")
    @JsonProperty("timestamp")
    private String hitDate;

}
