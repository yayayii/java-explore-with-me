package ru.practicum.explorewithme.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.EventState;
import ru.practicum.explorewithme.model.event.EventUpdateState;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
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
        "server.port=8081"
})
public class PrivateServiceTest {
    private final EntityManager entityManager;
    private final PrivateService privateService;
    private final AdminService adminService;

    private static CategoryRequestDto testCategoryRequestDto;
    private static UserRequestDto[] testUserRequestDtos;
    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto testEventRequestDto;
    private static EventUpdateRequestDto testEventUpdateRequestDto;
    private static EventShortResponseDto testEventShortResponseDto;
    private static EventResponseDto[] testEventResponseDtos;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDto = new CategoryRequestDto("name1");
        testUserRequestDtos = new UserRequestDto[]{
                new UserRequestDto("email1@yandex.ru", "name1"),
                new UserRequestDto("email2@yandex.ru", "name2")
        };
        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventRequestDto = new EventRequestDto(
                "title1", "annotation1", "description1", false, false,
                1, testLocalDateTime, new LocationDto(1.1, 1.1), 1L
        );
        testEventUpdateRequestDto = new EventUpdateRequestDto(
                "newTitle1", "newAnnotation1", "newDescription1",
                true, true, 2, testLocalDateTime,
                new LocationDto(0.0, 0.0), 1L, EventUpdateState.CANCEL_REVIEW
        );
        testEventShortResponseDto = new EventShortResponseDto(
                1L, "title1", "annotation1", false,
                new CategoryResponseDto(1L, "name1"), 0, testLocalDateTime, 0,
                new UserResponseDto(1L, "email1@yandex.ru", "name1")
        );
        testEventResponseDtos = new EventResponseDto[] {
                new EventResponseDto(
                        1L, "title1", "annotation1", "description1", false,
                        false, new CategoryResponseDto(1L, "name1"), 1,
                        0, testLocalDateTime, testLocalDateTime, null,
                        new LocationDto(1.1, 1.1), 0,
                        new UserResponseDto(1L, "email1@yandex.ru", "name1"), EventState.PENDING
                ),
                new EventResponseDto(
                        1L, "newTitle1", "newAnnotation1", "newDescription1", true,
                        true, new CategoryResponseDto(1L, "name1"), 2, 0,
                        testLocalDateTime, testLocalDateTime, null, new LocationDto(0.0, 0.0), 0,
                        new UserResponseDto(1L, "email1@yandex.ru", "name1"), EventState.CANCELED
                )
        };
    }

    @BeforeEach
    public void beforeEach() {
        entityManager.createNativeQuery(
            "delete from event; " +
            "alter table event " +
            "   alter column id " +
            "       restart with 1; " +
            "delete from user_account; " +
            "alter table user_account " +
            "   alter column id " +
            "       restart with 1; " +
            "delete from category; " +
            "alter table category " +
            "   alter column id " +
            "       restart with 1; "
        ).executeUpdate();
    }


    //events
    @Test
    public void testAddEvent() {
        assertThrows(NoSuchElementException.class, () -> privateService.addEvent(1L, testEventRequestDto));
        adminService.addUser(testUserRequestDtos[0]);

        assertThrows(NoSuchElementException.class, () -> privateService.addEvent(1L, testEventRequestDto));
        adminService.addCategory(testCategoryRequestDto);

        testEventRequestDto.setEventDate(LocalDateTime.now());
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.addEvent(1L, testEventRequestDto)
        );
        testEventRequestDto.setEventDate(testLocalDateTime);

        assertEquals(testEventResponseDtos[0], privateService.addEvent(1L, testEventRequestDto));
    }

    @Test
    public void testGetEventById() {
        assertThrows(NoSuchElementException.class, () -> privateService.getEventById(1L, 1L));
        adminService.addUser(testUserRequestDtos[0]);
        adminService.addUser(testUserRequestDtos[1]);

        assertThrows(NoSuchElementException.class, () -> privateService.getEventById(1L, 1L));
        adminService.addCategory(testCategoryRequestDto);
        privateService.addEvent(1L, testEventRequestDto);

        assertThrows(IllegalArgumentException.class, () -> privateService.getEventById(2L, 1L));

        assertEquals(testEventResponseDtos[0], privateService.getEventById(1L, 1L));
    }

    @Test
    public void testGetEventsByInitiatorId() {
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.getEventsByInitiatorId(1L, 1, 1)
        );
        adminService.addUser(testUserRequestDtos[0]);
        adminService.addCategory(testCategoryRequestDto);
        privateService.addEvent(1L, testEventRequestDto);
        privateService.addEvent(1L, testEventRequestDto);

        assertEquals(List.of(testEventShortResponseDto), privateService.getEventsByInitiatorId(1L, 0, 1));
    }

    @Test
    public void testUpdateEvent() {
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.updateEvent(1L, 1L, testEventUpdateRequestDto)
        );
        adminService.addUser(testUserRequestDtos[0]);

        assertThrows(
                NoSuchElementException.class,
                () -> privateService.updateEvent(1L, 1L, testEventUpdateRequestDto)
        );
        adminService.addCategory(testCategoryRequestDto);
        privateService.addEvent(1L, testEventRequestDto);

        testEventUpdateRequestDto.setEventDate(LocalDateTime.now());
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.updateEvent(1L, 1L, testEventUpdateRequestDto)
        );
        testEventUpdateRequestDto.setEventDate(testLocalDateTime);

        testEventUpdateRequestDto.setCategory(2L);
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.updateEvent(1L, 1L, testEventUpdateRequestDto)
        );
        testEventUpdateRequestDto.setCategory(1L);

        assertEquals(testEventResponseDtos[1], privateService.updateEvent(1L, 1L, testEventUpdateRequestDto));
    }
}
