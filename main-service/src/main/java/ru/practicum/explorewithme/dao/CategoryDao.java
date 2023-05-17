package ru.practicum.explorewithme.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.Category;

@Repository
public interface CategoryDao extends JpaRepository<Category, Long> {
}