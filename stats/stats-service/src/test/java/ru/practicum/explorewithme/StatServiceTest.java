package ru.practicum.explorewithme;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.model.StatModel;
import ru.practicum.explorewithme.service.StatService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatServiceTest {
    private final EntityManager entityManager;
    private final StatService statService;

    private static LocalDateTime testLocalDateTime;
    private static StatRequestDto[] testStatRequestDtos;
    private static StatResponseDto[] testStatResponseDtos;


    @BeforeAll
    public static void beforeAll() {
        testLocalDateTime = LocalDateTime.of(2001, 1, 1, 1, 1);
        testStatRequestDtos = new StatRequestDto[]{
                new StatRequestDto("app1", "uri1", "ip1", testLocalDateTime),
                new StatRequestDto("app2", "uri2", "ip2", testLocalDateTime)
        };
        testStatResponseDtos = new StatResponseDto[]{
                new StatResponseDto("app1", "uri1", 1L),
                new StatResponseDto("app1", "uri1", 2L),
                new StatResponseDto("app2", "uri2", 1L),
                new StatResponseDto("app2", "uri2", 2L)
        };
    }


    @Test
    public void testSaveEndpointRequest() {
        statService.saveEndpointRequest(testStatRequestDtos[0]);
        StatModel statModel = entityManager.createQuery(
                "select sm from StatModel sm where sm.id = ?1", StatModel.class
        ).setParameter(1, 1L).getSingleResult();

        assertEquals(1, statModel.getId());
        assertEquals(testStatRequestDtos[0].getApp(), statModel.getApp());
        assertEquals(testStatRequestDtos[0].getUri(), statModel.getUri());
        assertEquals(testStatRequestDtos[0].getIp(), statModel.getIp());
        assertEquals(testStatRequestDtos[0].getTimestamp(), statModel.getCreated());
    }

    @Test
    public void testGetStats() {
        assertThrows(
                IllegalArgumentException.class,
                () -> statService.getStats(testLocalDateTime, testLocalDateTime.minusDays(1), null, false)
        );

        statService.saveEndpointRequest(testStatRequestDtos[0]);
        statService.saveEndpointRequest(testStatRequestDtos[0]);
        statService.saveEndpointRequest(testStatRequestDtos[1]);
        statService.saveEndpointRequest(testStatRequestDtos[1]);

        assertEquals(
                List.of(testStatResponseDtos[0]),
                statService.getStats(
                        testLocalDateTime.minusDays(1),
                        testLocalDateTime.plusDays(1),
                        List.of(testStatRequestDtos[0].getUri()),
                        true
                )
        );

        assertEquals(
                List.of(testStatResponseDtos[1]),
                statService.getStats(
                        testLocalDateTime.minusDays(1),
                        testLocalDateTime.plusDays(1),
                        List.of(testStatRequestDtos[0].getUri()),
                        false
                )
        );

        assertEquals(
                List.of(testStatResponseDtos[0], testStatResponseDtos[2]),
                statService.getStats(
                        testLocalDateTime.minusDays(1),
                        testLocalDateTime.plusDays(1),
                        null,
                        true
                )
        );

        assertEquals(
                List.of(testStatResponseDtos[1], testStatResponseDtos[3]),
                statService.getStats(
                        testLocalDateTime.minusDays(1),
                        testLocalDateTime.plusDays(1),
                        null,
                        false
                )
        );
    }
}
