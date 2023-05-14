package ru.practicum.explorewithme;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.StatFullResponseDto;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.model.StatModel;
import ru.practicum.explorewithme.service.StatService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@AllArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = {
        "spring.config.activate.on-profile=ci,test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:name",
        "spring.datasource.username=test",
        "spring.datasource.password=test",
        "server.port=9091"
})
public class StatIntegrationTest {
    private final EntityManager entityManager;
    private final StatService statService;

    private static LocalDateTime testLocalDateTime;
    private static StatRequestDto[] testStatRequestDto;
    private static StatResponseDto[] testStatResponseDto;
    private static StatFullResponseDto testStatFullResponseDto;


    @BeforeAll
    public static void beforeAll() {
        testLocalDateTime = LocalDateTime.of(2001, 1, 1, 1, 1);
        testStatRequestDto = new StatRequestDto[]{
                new StatRequestDto("app1", "uri1", "ip1", testLocalDateTime),
                new StatRequestDto("app2", "uri2", "ip2", testLocalDateTime)
        };
        testStatResponseDto = new StatResponseDto[]{
                new StatResponseDto("app1", "uri1", 1L),
                new StatResponseDto("app1", "uri1", 2L),
                new StatResponseDto("app2", "uri2", 1L),
                new StatResponseDto("app2", "uri2", 2L)
        };
        testStatFullResponseDto = new StatFullResponseDto(1L, "app1", "uri1", "ip1", testLocalDateTime);
    }

    @AfterEach
    void afterEach() {
        entityManager.createNativeQuery(
                "delete from stat_model; " +
                "alter table stat_model " +
                "   alter column id " +
                "       restart with 1;"
        ).executeUpdate();
    }


    @Test
    public void testSaveEndpointRequest() {
        statService.saveEndpointRequest(testStatRequestDto[0]);
        StatModel statModel = entityManager.createQuery(
                "select sm from StatModel sm where sm.id = ?1", StatModel.class
        ).setParameter(1, 1L).getSingleResult();

        assertEquals(1, statModel.getId());
        assertEquals(testStatRequestDto[0].getApp(), statModel.getApp());
        assertEquals(testStatRequestDto[0].getUri(), statModel.getUri());
        assertEquals(testStatRequestDto[0].getIp(), statModel.getIp());
        assertEquals(testStatRequestDto[0].getCreated(), statModel.getCreated());
    }

    @Test
    public void testGetStatById() {
        Exception exception = assertThrows(NoSuchElementException.class, () -> statService.getStatById(1L));
        assertEquals("Stat id = 1 doesn't exist", exception.getMessage());

        statService.saveEndpointRequest(testStatRequestDto[0]);
        assertEquals(testStatFullResponseDto, statService.getStatById(1L));
    }

    @Test
    public void testGetStats() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> statService.getStats(testLocalDateTime, testLocalDateTime.minusDays(1), null, false)
        );
        assertEquals("Start date must be before end date", exception.getMessage());

        statService.saveEndpointRequest(testStatRequestDto[0]);
        statService.saveEndpointRequest(testStatRequestDto[0]);
        statService.saveEndpointRequest(testStatRequestDto[1]);
        statService.saveEndpointRequest(testStatRequestDto[1]);

        List<StatResponseDto> statResponseDtos = statService.getStats(
                testLocalDateTime.plusDays(1), testLocalDateTime.plusDays(2), null, false
        );
        assertEquals(Collections.emptyList(), statResponseDtos);

        statResponseDtos = statService.getStats(
                testLocalDateTime.minusDays(1),
                testLocalDateTime.plusDays(1),
                new String[]{testStatRequestDto[0].getUri()},
                true
        );
        assertEquals(List.of(testStatResponseDto[0]), statResponseDtos);

        statResponseDtos = statService.getStats(
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), null, true
        );
        assertEquals(List.of(testStatResponseDto[0], testStatResponseDto[2]), statResponseDtos);

        statResponseDtos = statService.getStats(
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1),
                new String[]{testStatRequestDto[0].getUri()},
                false
        );
        assertEquals(List.of(testStatResponseDto[1]), statResponseDtos);

        statResponseDtos = statService.getStats(
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), null, false
        );
        assertEquals(List.of(testStatResponseDto[1], testStatResponseDto[3]), statResponseDtos);
    }
}
