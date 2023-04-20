package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
    User toUser(UserDto userModelDto);

    UserDto toUserDto(User user);
}
