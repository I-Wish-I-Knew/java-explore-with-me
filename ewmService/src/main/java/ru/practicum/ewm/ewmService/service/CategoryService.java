package ru.practicum.ewm.ewmService.service;

import ru.practicum.ewm.ewmService.model.category.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAll(int from, int size);

    CategoryDto get(long id);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto save(CategoryDto categoryDto);

    void delete(long id);
}
