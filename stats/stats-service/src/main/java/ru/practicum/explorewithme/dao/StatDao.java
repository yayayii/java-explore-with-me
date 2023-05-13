package ru.practicum.explorewithme.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.StatModel;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatDao extends JpaRepository<StatModel, Long> {
    @Query(
            "select sm.app, sm.uri, count(sm.ip) as hits " +
            "from StatModel sm " +
            "where sm.created between ?1 and ?2 " +
            "group by sm.app, sm.uri "
    )
    List<Object[]> getStatModel(LocalDateTime start, LocalDateTime end);

    @Query(
            "select sm.app, sm.uri, count(distinct sm.ip) as hits " +
            "from StatModel sm " +
            "where sm.created between ?1 and ?2 " +
            "group by sm.app, sm.uri "
    )
    List<Object[]> getStatModelWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query(
            "select sm.app, sm.uri, count(sm.ip) as hits " +
            "from StatModel sm " +
            "where sm.created between ?1 and ?2 " +
            "and sm.uri in ?3 " +
            "group by sm.app, sm.uri "
    )
    List<Object[]> getStatModelInUris(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query(
            "select sm.app, sm.uri, count(distinct sm.ip) as hits " +
            "from StatModel sm " +
            "where sm.created between ?1 and ?2 " +
            "and sm.uri in ?3 " +
            "group by sm.app, sm.uri "
    )
    List<Object[]> getStatModelInUrisWithUniqueIp(LocalDateTime start, LocalDateTime end, String[] uris);
}
