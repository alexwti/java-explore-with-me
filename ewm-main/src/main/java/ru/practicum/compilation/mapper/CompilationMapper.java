package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;

@Mapper(componentModel = "spring")
@Component
public interface CompilationMapper {
    CompilationDto toCompilationDto(Compilation compilation);
}
