package ru.practicum.ewm.ewmService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ewmService.model.category.CategoryDto;
import ru.practicum.ewm.ewmService.service.CategoryService;
import ru.practicum.ewm.ewmService.utility.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping("/admin/categories")
    public CategoryDto save(@RequestBody @Valid CategoryDto categoryDto) {

        log.info("Save category {}", categoryDto);
        return service.save(categoryDto);
    }

    @PatchMapping("/admin/categories")
    public CategoryDto update(@RequestBody @Validated(Update.class) CategoryDto categoryDto) {
        log.info("Update category {}", categoryDto);
        return service.update(categoryDto);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAll(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Get all categories from {} size {}", from, size);
        return service.getAll(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto get(@PathVariable Long catId) {
        log.info("Get category {}", catId);
        return service.get(catId);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public void delete(@PathVariable Long catId) {
        log.info("Delete category {}", catId);
        service.delete(catId);
    }
}
