package ru.practicum.ewm.ewmService.model.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;
}
