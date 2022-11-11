package ru.practicum.ewm.ewmService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.ewmService.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
