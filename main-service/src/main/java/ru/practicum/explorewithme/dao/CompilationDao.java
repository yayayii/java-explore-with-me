package ru.practicum.explorewithme.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.Compilation;

import java.util.List;

@Repository
public interface CompilationDao extends JpaRepository<Compilation, Long> {
    @Query(
            "select c from Compilation c " +
            "where (c.pinned = :isPinned or :isPinned is null)"
    )
    List<Compilation> findAllByPinned(Boolean isPinned, Pageable pageable);
}
