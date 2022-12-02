package ru.practicum.ewm.ewmService.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.model.user.UserDto;
import ru.practicum.ewm.ewmService.model.user.UserShortDto;

@UtilityClass
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
