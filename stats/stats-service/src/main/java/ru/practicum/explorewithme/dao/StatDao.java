package ru.practicum.explorewithme.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.StatModel;
import ru.practicum.explorewithme.model.StatProjection;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatDao extends JpaRepository<StatModel, Long> {
    @Query(
            "select new ru.practicum.explorewithme.model.StatProjection(sm.app, sm.uri, count(sm.ip) as hits) " +
            "from StatModel sm " +
            "where sm.created between :start and :end " +
            "group by sm.app, sm.uri "
    )
    List<StatProjection> getStatModel(LocalDateTime start, LocalDateTime end, Sort sort);

    @Query(
            "select new ru.practicum.explorewithme.model.StatProjection(sm.app, sm.uri, count(distinct sm.ip) as hits) " +
            "from StatModel sm " +
            "where sm.created between :start and :end " +
            "group by sm.app, sm.uri "
    )
    List<StatProjection> getStatModelWithUniqueIp(LocalDateTime start, LocalDateTime end, Sort sort);

    @Query(
            "select new ru.practicum.explorewithme.model.StatProjection(sm.app, sm.uri, count(sm.ip) as hits) " +
            "from StatModel sm " +
            "where sm.created between :start and :end " +
            "and sm.uri in (:uris) " +
            "group by sm.app, sm.uri "
    )
    List<StatProjection> getStatModelInUris(LocalDateTime start, LocalDateTime end, List<String> uris, Sort sort);

    @Query(
            "select new ru.practicum.explorewithme.model.StatProjection(sm.app, sm.uri, count(distinct sm.ip) as hits) " +
            "from StatModel sm " +
            "where sm.created between :start and :end " +
            "and sm.uri in (:uris) " +
            "group by sm.app, sm.uri "
    )
    List<StatProjection> getStatModelInUrisWithUniqueIp(
            LocalDateTime start, LocalDateTime end, List<String> uris, Sort sort
    );
}
