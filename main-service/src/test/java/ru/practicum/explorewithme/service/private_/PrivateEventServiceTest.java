package ru.practicum.explorewithme.service.private_;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.admin.AdminCategoryService;
import ru.practicum.explorewithme.service.admin.AdminEventService;
import ru.practicum.explorewithme.service.admin.AdminUserService;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PrivateEventServiceTest {
    private final AdminCategoryService adminCategoryService;
    private final AdminEventService adminEventService;
    private final AdminUserService adminUserService;
    private final PrivateEventSerivce privateService;

    private static CategoryRequestDto[] testCategoryRequestDtos;
    private static UserRequestDto[] testUserRequestDtos;
    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto testEventRequestDto;
    private static EventUpdateRequestDto testEventUserUpdateRequestDto;
    private static EventUpdateRequestDto testEventAdminUpdateRequestDto;
    private static EventShortResponseDto testEventShortResponseDto;
    private static EventResponseDto[] testEventResponseDtos;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDtos = new CategoryRequestDto[]{
                new CategoryRequestDto("name1"),
                new CategoryRequestDto("name2")
        };

        testUserRequestDtos = new UserRequestDto[]{
                new UserRequestDto("email1@yandex.ru", "name1"),
                new UserRequestDto("email2@yandex.ru", "name2")
        };

        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventRequestDto = new EventRequestDto(
                "title1", "annotation1", "description1", false, false,
                1, testLocalDateTime, new LocationDto(1.1, 1.1), 1L
        );
        testEventUserUpdateRequestDto = new EventUpdateRequestDto(
                "newTitle1", "newAnnotation1", "newDescription1",
                true, true, 2, testLocalDateTime,
                new LocationDto(0.0, 0.0), 2L, EventUpdateState.CANCEL_REVIEW
        );
        testEventAdminUpdateRequestDto = new EventUpdateRequestDto(
                "title1", "annotation1", "description1",
                false, false, 1, testLocalDateTime,
                new LocationDto(1.1, 1.1), 1L, EventUpdateState.PUBLISH_EVENT
        );
        testEventShortResponseDto = new EventShortResponseDto(
                1L, "title1", "annotation1", false,
                new CategoryResponseDto(1L, "name1"), 0, testLocalDateTime, 0,
                new UserResponseDto(1L, "email1@yandex.ru", "name1"),
                EventState.PENDING, testLocalDateTime
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
                        true, new CategoryResponseDto(2L, "name2"), 2, 0,
                        testLocalDateTime, testLocalDateTime, null, new LocationDto(0.0, 0.0), 0,
                        new UserResponseDto(1L, "email1@yandex.ru", "name1"), EventState.CANCELED
                )
        };
    }


    @Test
    public void testAddEvent() {
        assertThrows(NoSuchElementException.class, () -> privateService.addEvent(1L, testEventRequestDto));

        adminUserService.addUser(testUserRequestDtos[0]);
        assertThrows(NoSuchElementException.class, () -> privateService.addEvent(1L, testEventRequestDto));

        adminCategoryService.addCategory(testCategoryRequestDtos[0]);
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

        adminUserService.addUser(testUserRequestDtos[0]);
        assertThrows(NoSuchElementException.class, () -> privateService.getEventById(1L, 1L));

        adminUserService.addUser(testUserRequestDtos[1]);
        adminCategoryService.addCategory(testCategoryRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDto);
        assertThrows(DataIntegrityViolationException.class, () -> privateService.getEventById(2L, 1L));

        assertEquals(testEventResponseDtos[0], privateService.getEventById(1L, 1L));
    }

    @Test
    public void testGetEventsByInitiatorId() {
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.getEventsByInitiatorId(1L, 1, 1)
        );

        adminUserService.addUser(testUserRequestDtos[0]);
        adminCategoryService.addCategory(testCategoryRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDto);
        privateService.addEvent(1L, testEventRequestDto);
        assertEquals(List.of(testEventShortResponseDto), privateService.getEventsByInitiatorId(1L, 0, 1));
    }

    @Test
    public void testUpdateEvent() {
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.updateEvent(1L, 1L, testEventUserUpdateRequestDto)
        );

        adminUserService.addUser(testUserRequestDtos[0]);
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.updateEvent(1L, 1L, testEventUserUpdateRequestDto)
        );

        adminCategoryService.addCategory(testCategoryRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDto);
        adminUserService.addUser(testUserRequestDtos[1]);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.updateEvent(2L, 1L, testEventUserUpdateRequestDto)
        );

        privateService.addEvent(1L, testEventRequestDto);
        adminEventService.updateEvent(2L, testEventAdminUpdateRequestDto);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.updateEvent(1L, 2L, testEventUserUpdateRequestDto)
        );

        testEventUserUpdateRequestDto.setEventDate(LocalDateTime.now());
        assertThrows(
                IllegalArgumentException.class,
                () -> privateService.updateEvent(1L, 1L, testEventUserUpdateRequestDto)
        );
        testEventUserUpdateRequestDto.setEventDate(testLocalDateTime);

        assertThrows(
                NoSuchElementException.class,
                () -> privateService.updateEvent(1L, 1L, testEventUserUpdateRequestDto)
        );

        adminCategoryService.addCategory(testCategoryRequestDtos[1]);
        assertEquals(
                testEventResponseDtos[1],
                privateService.updateEvent(1L, 1L, testEventUserUpdateRequestDto)
        );

        testEventResponseDtos[1].setState(EventState.PENDING);
        testEventUserUpdateRequestDto.setStateAction(EventUpdateState.SEND_TO_REVIEW);
        assertEquals(
                testEventResponseDtos[1],
                privateService.updateEvent(1L, 1L, testEventUserUpdateRequestDto)
        );
        testEventResponseDtos[1].setState(EventState.CANCELED);
        testEventUserUpdateRequestDto.setStateAction(EventUpdateState.CANCEL_REVIEW);
    }
}
