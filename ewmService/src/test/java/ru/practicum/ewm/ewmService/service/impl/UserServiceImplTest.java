package ru.practicum.ewm.ewmService.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.mapper.UserMapper;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.model.user.UserDto;
import ru.practicum.ewm.ewmService.repository.UserRepository;
import ru.practicum.ewm.ewmService.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserService service;
    @Autowired
    private final UserRepository repository;

    @Test
    void getAll() {
        User user1 = repository.save(User.builder()
                .email("user1@email.com")
                .name("user1")
                .build());

        User user2 = repository.save(User.builder()
                .email("user2@email.com")
                .name("user2")
                .build());

        List<UserDto> users = service.getAll(Collections.emptyList(), 0, 10);

        assertThat(users).hasSize(2)
                .contains(UserMapper.toUserDto(user1))
                .contains(UserMapper.toUserDto(user2));
    }

    @Test
    void save() {

        UserDto userDto = service.save(UserDto.builder()
                .email("user1@email.com")
                .name("user1")
                .build());

        Optional<User> user = repository.findById(userDto.getId());

        assertThat(user).isNotEmpty();
        assertThat(user.get())
                .hasFieldOrPropertyWithValue("id", userDto.getId())
                .hasFieldOrPropertyWithValue("name", userDto.getName())
                .hasFieldOrPropertyWithValue("email", userDto.getEmail());
    }

    @Test
    void delete() {
        User user1 = repository.save(User.builder()
                .email("user1@email.com")
                .name("user1")
                .build());

        User user2 = repository.save(User.builder()
                .email("user2@email.com")
                .name("user2")
                .build());

        List<User> users = repository.findAll();

        assertThat(users).hasSize(2)
                .contains(user1)
                .contains(user2);

        service.delete(user1.getId());

        users = repository.findAll();

        assertThat(users).hasSize(1)
                .doesNotContain(user1)
                .contains(user2);
    }
}