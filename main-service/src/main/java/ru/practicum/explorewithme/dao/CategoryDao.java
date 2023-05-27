package ru.practicum.explorewithme.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.model.Category;

public interface CategoryDao extends JpaRepository<Category, Long> {
}