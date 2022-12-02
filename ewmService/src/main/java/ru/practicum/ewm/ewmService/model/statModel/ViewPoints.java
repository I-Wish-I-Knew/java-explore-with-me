package ru.practicum.ewm.ewmService.model.statModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewPoints {
    private String app;
    private String uri;
    private Long hits;
}
