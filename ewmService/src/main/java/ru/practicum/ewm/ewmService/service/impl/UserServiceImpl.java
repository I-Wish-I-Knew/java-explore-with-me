package ru.practicum.ewm.ewmService.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm.ewmService.mapper.UserMapper;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.model.user.UserDto;
import ru.practicum.ewm.ewmService.repository.UserRepository;
import ru.practicum.ewm.ewmService.service.UserService;
import ru.practicum.ewm.ewmService.utility.Page;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        List<User> users = CollectionUtils.isEmpty(ids) ? repository.findAll(Page.of(from, size)).getContent() :
                repository.findAllById(ids);


        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto save(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Transactional
    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}
