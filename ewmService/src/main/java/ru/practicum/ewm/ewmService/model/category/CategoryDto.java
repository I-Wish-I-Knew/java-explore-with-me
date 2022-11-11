package ru.practicum.ewm.ewmService.model.category;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.ewmService.utility.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CategoryDto {
    @NotNull(groups = {Update.class})
    private Long id;
    @NotBlank
    private String name;
}
