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
import ru.practicum.explorewithme.dto.request.EventRequestResponseDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateResponseDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enums.EventState;
import ru.practicum.explorewithme.dto.event.enums.EventUpdateState;
import ru.practicum.explorewithme.model.request.enums.EventRequestStatus;

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
        "server.port=8081"
})
public class PrivateServiceTest {
    private final EntityManager entityManager;
    private final PrivateService privateService;
    private final AdminService adminService;

    private static CategoryRequestDto[] testCategoryRequestDtos;
    private static UserRequestDto[] testUserRequestDtos;
    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto testEventRequestDto;
    private static EventUpdateRequestDto testEventUserUpdateRequestDto;
    private static EventUpdateRequestDto[] testEventAdminUpdateRequestDtos;
    private static EventShortResponseDto testEventShortResponseDto;
    private static EventResponseDto[] testEventResponseDtos;
    private static EventRequestUpdateRequestDto[] testEventRequestUpdateRequestDtos;
    private static EventRequestUpdateResponseDto testEventRequestUpdateResponseDto;
    private static EventRequestResponseDto[] testEventRequestResponseDtos;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDtos = new CategoryRequestDto[]{
                new CategoryRequestDto("name1"), new CategoryRequestDto("name2")
        };
        testUserRequestDtos = new UserRequestDto[]{
                new UserRequestDto("email1@yandex.ru", "name1"),
                new UserRequestDto("email2@yandex.ru", "name2"),
                new UserRequestDto("email3@yandex.ru", "name3"),
                new UserRequestDto("email4@yandex.ru", "name4")
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
        testEventAdminUpdateRequestDtos = new EventUpdateRequestDto[]{
                new EventUpdateRequestDto(
                        "title1", "annotation1", "description1",
                        false, false, 1, testLocalDateTime,
                        new LocationDto(1.1, 1.1), 1L, EventUpdateState.PUBLISH_EVENT
                ),
                new EventUpdateRequestDto(
                        "title1", "annotation1", "description1",
                        false, false, 3, testLocalDateTime,
                        new LocationDto(1.1, 1.1), 1L, EventUpdateState.PUBLISH_EVENT
                ),
                new EventUpdateRequestDto(
                        "title1", "annotation1", "description1",
                        false, true, 3, testLocalDateTime,
                        new LocationDto(1.1, 1.1), 1L, EventUpdateState.PUBLISH_EVENT
                )
        };
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
        testEventRequestUpdateRequestDtos = new EventRequestUpdateRequestDto[]{
                new EventRequestUpdateRequestDto(List.of(1L), EventRequestStatus.CONFIRMED),
                new EventRequestUpdateRequestDto(List.of(2L, 3L), EventRequestStatus.CONFIRMED)
        };
        testEventRequestResponseDtos = new EventRequestResponseDto[]{
                new EventRequestResponseDto(
                        3L, 2L, 3L, testLocalDateTime, EventRequestStatus.CONFIRMED
                ),
                new EventRequestResponseDto(
                        1L, 1L, 2L, testLocalDateTime, EventRequestStatus.CONFIRMED
                ),
                new EventRequestResponseDto(
                        2L, 2L, 2L, testLocalDateTime, EventRequestStatus.CONFIRMED
                ),
                new EventRequestResponseDto(
                        1L, 1L, 2L, testLocalDateTime, EventRequestStatus.CONFIRMED
                ),
                new EventRequestResponseDto(
                        2L, 1L, 3L, testLocalDateTime, EventRequestStatus.CONFIRMED
                ),
                new EventRequestResponseDto(
                        2L, 1L, 3L, testLocalDateTime, EventRequestStatus.CONFIRMED
                ),
                new EventRequestResponseDto(
                        3L, 1L, 4L, testLocalDateTime, EventRequestStatus.CONFIRMED
                ),
                new EventRequestResponseDto(
                        2L, 1L, 3L, testLocalDateTime, EventRequestStatus.CANCELED
                )
        };
        testEventRequestUpdateResponseDto = new EventRequestUpdateResponseDto(
                List.of(testEventRequestResponseDtos[5], testEventRequestResponseDtos[6]), Collections.emptyList()
        );
    }

    @BeforeEach
    public void beforeEach() {
        entityManager.createNativeQuery(
            "delete from event_compilation; " +
            "delete from compilation; " +
            "alter table compilation " +
            "   alter column id " +
            "       restart with 1; " +
            "delete from event_request; " +
            "alter table event_request " +
            "   alter column id " +
            "       restart with 1; " +
            "delete from event; " +
            "alter table event " +
            "   alter column id " +
            "       restart with 1; " +
            "delete from category; " +
            "alter table category " +
            "   alter column id " +
            "       restart with 1; " +
            "delete from user_account; " +
            "alter table user_account " +
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

        adminService.addCategory(testCategoryRequestDtos[0]);
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
        assertThrows(NoSuchElementException.class, () -> privateService.getEventById(1L, 1L));

        adminService.addUser(testUserRequestDtos[1]);
        adminService.addCategory(testCategoryRequestDtos[0]);
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

        adminService.addUser(testUserRequestDtos[0]);
        adminService.addCategory(testCategoryRequestDtos[0]);
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

        adminService.addUser(testUserRequestDtos[0]);
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.updateEvent(1L, 1L, testEventUserUpdateRequestDto)
        );

        adminService.addCategory(testCategoryRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDto);
        adminService.addUser(testUserRequestDtos[1]);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.updateEvent(2L, 1L, testEventUserUpdateRequestDto)
        );

        privateService.addEvent(1L, testEventRequestDto);
        adminService.updateEvent(2L, testEventAdminUpdateRequestDtos[0]);
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

        adminService.addCategory(testCategoryRequestDtos[1]);
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

    //requests
    @Test
    public void testAddRequest() {
        assertThrows(NoSuchElementException.class, () -> privateService.addRequest(1L, 1L));

        adminService.addUser(testUserRequestDtos[0]);
        assertThrows(NoSuchElementException.class, () -> privateService.addRequest(1L, 1L));

        adminService.addCategory(testCategoryRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDto);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.addRequest(1L, 1L)
        );

        adminService.addUser(testUserRequestDtos[1]);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.addRequest(2L, 1L)
        );

        adminService.updateEvent(1L, testEventAdminUpdateRequestDtos[0]);
        privateService.addRequest(2L, 1L);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.addRequest(2L, 1L)
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.addRequest(2L, 1L)
        );

        privateService.addEvent(1L, testEventRequestDto);
        adminService.updateEvent(2L, testEventAdminUpdateRequestDtos[1]);
        privateService.addRequest(2L, 2L);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.addRequest(2L, 2L)
        );

        adminService.addUser(testUserRequestDtos[2]);
        assertEquals(testEventRequestResponseDtos[0], privateService.addRequest(3L, 2L));
    }

    @Test
    public void testGetRequestsForUser() {
        assertThrows(NoSuchElementException.class, () -> privateService.getRequestsForUser(1L));

        adminService.addUser(testUserRequestDtos[0]);
        adminService.addUser(testUserRequestDtos[1]);
        adminService.addCategory(testCategoryRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDto);
        adminService.updateEvent(1L, testEventAdminUpdateRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDto);
        adminService.updateEvent(2L, testEventAdminUpdateRequestDtos[0]);
        privateService.addRequest(2L, 1L);
        privateService.addRequest(2L, 2L);
        assertEquals(
                List.of(testEventRequestResponseDtos[1], testEventRequestResponseDtos[2]),
                privateService.getRequestsForUser(2L)
        );
    }

    @Test
    public void testGetRequestsForEvent() {
        assertThrows(NoSuchElementException.class, () -> privateService.getRequestsForEvent(1L, 1L));

        adminService.addUser(testUserRequestDtos[0]);
        assertThrows(NoSuchElementException.class, () -> privateService.getRequestsForEvent(1L, 1L));

        adminService.addUser(testUserRequestDtos[1]);
        adminService.addCategory(testCategoryRequestDtos[1]);
        privateService.addEvent(1L, testEventRequestDto);
        assertThrows(DataIntegrityViolationException.class, () -> privateService.getRequestsForEvent(2L, 1L));

        adminService.addUser(testUserRequestDtos[2]);
        adminService.updateEvent(1L, testEventAdminUpdateRequestDtos[1]);
        privateService.addRequest(2L, 1L);
        privateService.addRequest(3L, 1L);
        assertEquals(
                List.of(testEventRequestResponseDtos[3], testEventRequestResponseDtos[4]),
                privateService.getRequestsForEvent(1L, 1L)
        );
    }

    @Test
    public void testModerateRequests() {
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        adminService.addUser(testUserRequestDtos[0]);
        assertThrows(
                NoSuchElementException.class,
                () -> privateService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        adminService.addCategory(testCategoryRequestDtos[1]);
        privateService.addEvent(1L, testEventRequestDto);
        adminService.addUser(testUserRequestDtos[1]);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.moderateRequests(2L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        assertThrows(
                NoSuchElementException.class,
                () -> privateService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        adminService.updateEvent(1L, testEventAdminUpdateRequestDtos[2]);
        privateService.addRequest(2L, 1L);
        privateService.addEvent(1L, testEventRequestDto);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.moderateRequests(1L, 2L, testEventRequestUpdateRequestDtos[0])
        );

        privateService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0]);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        adminService.addUser(testUserRequestDtos[2]);
        adminService.addUser(testUserRequestDtos[3]);
        privateService.addRequest(3L, 1L);
        privateService.addRequest(4L, 1L);
        assertEquals(
                testEventRequestUpdateResponseDto,
                privateService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[1])
        );
    }

    @Test
    public void testCancelRequest() {
        assertThrows(NoSuchElementException.class, () -> privateService.cancelRequest(1L, 1L));

        adminService.addUser(testUserRequestDtos[0]);
        assertThrows(NoSuchElementException.class, () -> privateService.cancelRequest(1L, 1L));

        adminService.addCategory(testCategoryRequestDtos[1]);
        privateService.addEvent(1L, testEventRequestDto);
        adminService.addUser(testUserRequestDtos[1]);
        adminService.updateEvent(1L, testEventAdminUpdateRequestDtos[2]);
        privateService.addRequest(2L, 1L);
        assertThrows(DataIntegrityViolationException.class, () -> privateService.cancelRequest(1L, 1L));

        privateService.cancelRequest(2L, 1L);
        assertThrows(DataIntegrityViolationException.class, () -> privateService.cancelRequest(2L, 1L));

        adminService.addUser(testUserRequestDtos[2]);
        privateService.addRequest(3L, 1L);
        assertEquals(
                testEventRequestResponseDtos[7],
                privateService.cancelRequest(3L, 2L)
        );
    }
}
