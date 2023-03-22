package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Builder
@Getter
@Setter
public class CreateEndpointHitDto {
    @NotNull(message = "field app can't be null")
    private String app;
    @NotNull(message = "field uri can't be null")
    private String uri;
    @NotNull(message = "field ip can't be null")
    private String ip;
    private String timestamp;
}
