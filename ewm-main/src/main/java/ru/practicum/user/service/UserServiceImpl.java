package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByName(userDto.getName())) {
            log.info(String.format("Can't create user with name: %s, the name was used by another user", userDto.getName()));
            throw new ConflictException(String.format("Can't create user with name: %s, the name was used by another user",
                    userDto.getName()));
        }
        User user = userRepository.save(userMapper.toUser(userDto));
        log.info(String.format("The user %s was created", userDto.getName()));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        log.info("A list of users has been sent");
        if (ids != null && ids.size() > 0) {
            return userRepository.findAllByIdIn(ids, page).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            List<User> result = userRepository.findAll(page).toList();
            return userRepository.findAll(page).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        log.info(String.format("User with id: %s was deleted"), id);
        userRepository.deleteById(id);
    }
}
