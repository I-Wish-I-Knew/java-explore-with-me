package ru.practicum.ewm.ewmService.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.exception.ForbiddenException;
import ru.practicum.ewm.ewmService.exception.NotFoundException;
import ru.practicum.ewm.ewmService.mapper.CategoryMapper;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.category.CategoryDto;
import ru.practicum.ewm.ewmService.repository.CategoryRepository;
import ru.practicum.ewm.ewmService.repository.EventRepository;
import ru.practicum.ewm.ewmService.service.CategoryService;
import ru.practicum.ewm.ewmService.utility.Page;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.ewmService.utility.Constants.CATEGORY_NOT_FOUND;

@Transactional(readOnly = true)
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    public CategoryServiceImpl(CategoryRepository repository, EventRepository eventRepository) {
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        List<Category> categories = repository.findAll(Page.of(from, size)).getContent();
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto get(long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND, id)));
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        return CategoryMapper.toCategoryDto(repository.save(category));
    }

    @Transactional
    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        long id = categoryDto.getId();
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format(CATEGORY_NOT_FOUND, id));
        }
        Category category = CategoryMapper.toCategory(categoryDto);
        return CategoryMapper.toCategoryDto(repository.save(category));
    }

    @Transactional
    @Override
    public void delete(long id) {
        if (Boolean.TRUE.equals(eventRepository.existsByCategoryId(id))) {
            throw new ForbiddenException("Category can not be deleted because it has related events");
        }
        repository.deleteById(id);
    }
}
