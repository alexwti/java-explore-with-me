package ru.practicum.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UpdateCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    private String title;
}
