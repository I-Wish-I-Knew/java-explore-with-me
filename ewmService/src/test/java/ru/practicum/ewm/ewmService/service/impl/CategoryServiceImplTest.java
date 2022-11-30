package ru.practicum.ewm.ewmService.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.mapper.CategoryMapper;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.category.CategoryDto;
import ru.practicum.ewm.ewmService.repository.CategoryRepository;
import ru.practicum.ewm.ewmService.service.CategoryService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryServiceImplTest {

    private final CategoryService service;
    @Autowired
    private final CategoryRepository repository;

    @Test
    void getAll() {
        Category category1 = repository.save(Category.builder().name("category1").build());
        Category category2 = repository.save(Category.builder().name("category2").build());

        List<CategoryDto> categories = service.getAll(0, 10);

        assertThat(categories).hasSize(2)
                .contains(CategoryMapper.toCategoryDto(category1))
                .contains(CategoryMapper.toCategoryDto(category2));
    }

    @Test
    void get() {
        Category category = repository.save(Category.builder().name("category").build());
        CategoryDto categoryDto = service.get(category.getId());

        assertThat(categoryDto).isEqualTo(CategoryMapper.toCategoryDto(category));
    }

    @Test
    void save() {
        CategoryDto categoryDto = service.save(CategoryDto.builder().name("category").build());
        Optional<Category> category = repository.findById(categoryDto.getId());

        assertThat(category).isNotEmpty();
        assertThat(category.get())
                .hasFieldOrPropertyWithValue("name", categoryDto.getName())
                .hasFieldOrPropertyWithValue("id", categoryDto.getId());
    }

    @Test
    void update() {
        Category category = repository.save(Category.builder().name("category").build());
        Optional<Category> categoryFromDb = repository.findById(category.getId());

        assertThat(categoryFromDb).isNotEmpty()
                .contains(category);

        CategoryDto categoryForUpdate = CategoryMapper.toCategoryDto(category);
        categoryForUpdate.setName("updatedCategory");
        CategoryDto updatedCategory = service.update(categoryForUpdate);

        assertThat(updatedCategory).hasFieldOrPropertyWithValue("id", category.getId())
                .hasFieldOrPropertyWithValue("name", categoryForUpdate.getName());
    }

    @Test
    void delete() {
        Category category1 = repository.save(Category.builder().name("category1").build());
        Category category2 = repository.save(Category.builder().name("category2").build());

        List<Category> categories = repository.findAll();

        assertThat(categories).hasSize(2)
                .contains(category1)
                .contains(category2);

        service.delete(category1.getId());

        categories = repository.findAll();

        assertThat(categories).hasSize(1)
                .doesNotContain(category1)
                .contains(category2);
    }
}