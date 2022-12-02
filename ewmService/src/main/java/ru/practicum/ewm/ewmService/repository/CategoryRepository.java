package ru.practicum.ewm.ewmService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.ewmService.model.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
