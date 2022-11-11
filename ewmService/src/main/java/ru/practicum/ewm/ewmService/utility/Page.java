package ru.practicum.ewm.ewmService.utility;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Page {
    public static Pageable of(int from, int size) {
        return PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
    }
}
