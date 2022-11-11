package ru.practicum.ewm.ewmService.service;

import ru.practicum.ewm.ewmService.model.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll(List<Long> ids, int from, int size);

    UserDto save(UserDto userDto);

    void delete(long id);
}
