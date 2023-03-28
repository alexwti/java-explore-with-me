package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class CreateEndpointHitDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
