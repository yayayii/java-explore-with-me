package ru.practicum.explorewithme.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.LocationDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.EventState;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
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
        "server.port=8081"
})
public class PrivateServiceTest {
    private final EntityManager entityManager;
    private final PrivateService privateService;
    private final AdminService adminService;

    private static CategoryRequestDto testCategoryRequestDto;
    private static UserRequestDto testUserRequestDto;
    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto testEventRequestDto;
    private static EventResponseDto testEventResponseDto;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDto = new CategoryRequestDto("name1");
        testUserRequestDto = new UserRequestDto("email1@yandex.ru", "name1");
        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventRequestDto = new EventRequestDto(
                "title1", "annotation1", "description1", false, false,
                1, testLocalDateTime, new LocationDto(1.1, 1.1), 1L
        );
        testEventResponseDto = new EventResponseDto(
                1L, "title1", "annotation1", "description1", false,
                false, new CategoryResponseDto(1L, "name1"), 1, 0,
                testLocalDateTime, testLocalDateTime, null, new LocationDto(1.1, 1.1), 0,
                new UserResponseDto(1L, "email1@yandex.ru", "name1"), EventState.PUBLISHED
        );
    }

    @BeforeEach
    public void beforeEach() {
        entityManager.createNativeQuery(
            "delete from event; " +
            "alter table event " +
            "   alter column id " +
            "       restart with 1; "
        ).executeUpdate();
    }


    //events
    @Test
    public void testAddEvent() {
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.addEvent(1L, testEventRequestDto)
        );
        adminService.addUser(testUserRequestDto);

        assertThrows(
                NoSuchElementException.class,
                () -> privateService.addEvent(1L, testEventRequestDto)
        );
        adminService.addCategory(testCategoryRequestDto);

        testEventRequestDto.setEventDate(LocalDateTime.now());
        assertThrows(
                IllegalArgumentException.class,
                () -> privateService.addEvent(1L, testEventRequestDto)
        );
        testEventRequestDto.setEventDate(testLocalDateTime);

        assertEquals(testEventResponseDto, privateService.addEvent(1L, testEventRequestDto));
    }
}
