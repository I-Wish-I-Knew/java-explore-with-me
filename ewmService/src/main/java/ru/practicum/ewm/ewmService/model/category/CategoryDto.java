package ru.practicum.ewm.ewmService.model.category;

import lombok.*;
import ru.practicum.ewm.ewmService.utility.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class CategoryDto {
    @NotNull(groups = {Update.class})
    private Long id;
    @NotBlank
    private String name;
}
