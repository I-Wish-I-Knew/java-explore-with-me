package ru.practicum.ewm.statisticService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ViewPoints {
    private String app;
    private String uri;
    private Long hits;
}
