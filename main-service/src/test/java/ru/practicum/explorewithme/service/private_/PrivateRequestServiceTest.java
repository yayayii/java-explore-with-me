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
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.request.EventRequestResponseDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateResponseDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.model.request.enum_.EventRequestStatus;
import ru.practicum.explorewithme.service.admin.AdminCategoryService;
import ru.practicum.explorewithme.service.admin.AdminEventService;
import ru.practicum.explorewithme.service.admin.AdminUserService;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PrivateRequestServiceTest {
    private final AdminCategoryService adminCategoryService;
    private final AdminEventService adminEventService;
    private final AdminUserService adminUserService;
    private final PrivateEventService privateEventSerivce;
    private final PrivateRequestService privateRequestService;

    private static CategoryRequestDto[] testCategoryRequestDtos;
    private static UserRequestDto[] testUserRequestDtos;
    private static EventRequestDto testEventRequestDto;
    private static EventUpdateRequestDto[] testEventAdminUpdateRequestDtos;
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

        LocalDateTime testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventRequestDto = new EventRequestDto(
                "title1", "annotation1", "description1", false, false,
                1, testLocalDateTime, new LocationDto(1.1, 1.1), 1L
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


    @Test
    public void testAddRequest() {
        assertThrows(NoSuchElementException.class, () -> privateRequestService.addRequest(1L, 1L));

        adminUserService.addUser(testUserRequestDtos[0]);
        assertThrows(NoSuchElementException.class, () -> privateRequestService.addRequest(1L, 1L));

        adminCategoryService.addCategory(testCategoryRequestDtos[0]);
        privateEventSerivce.addEvent(1L, testEventRequestDto);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateRequestService.addRequest(1L, 1L)
        );

        adminUserService.addUser(testUserRequestDtos[1]);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateRequestService.addRequest(2L, 1L)
        );

        adminEventService.updateEvent(1L, testEventAdminUpdateRequestDtos[0]);
        privateRequestService.addRequest(2L, 1L);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateRequestService.addRequest(2L, 1L)
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateRequestService.addRequest(2L, 1L)
        );

        privateEventSerivce.addEvent(1L, testEventRequestDto);
        adminEventService.updateEvent(2L, testEventAdminUpdateRequestDtos[1]);
        privateRequestService.addRequest(2L, 2L);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateRequestService.addRequest(2L, 2L)
        );

        adminUserService.addUser(testUserRequestDtos[2]);
        assertEquals(testEventRequestResponseDtos[0], privateRequestService.addRequest(3L, 2L));
    }

    @Test
    public void testGetRequestsForUser() {
        assertThrows(NoSuchElementException.class, () -> privateRequestService.getRequestsForUser(1L));

        adminUserService.addUser(testUserRequestDtos[0]);
        adminUserService.addUser(testUserRequestDtos[1]);
        adminCategoryService.addCategory(testCategoryRequestDtos[0]);
        privateEventSerivce.addEvent(1L, testEventRequestDto);
        adminEventService.updateEvent(1L, testEventAdminUpdateRequestDtos[0]);
        privateEventSerivce.addEvent(1L, testEventRequestDto);
        adminEventService.updateEvent(2L, testEventAdminUpdateRequestDtos[0]);
        privateRequestService.addRequest(2L, 1L);
        privateRequestService.addRequest(2L, 2L);
        assertEquals(
                List.of(testEventRequestResponseDtos[1], testEventRequestResponseDtos[2]),
                privateRequestService.getRequestsForUser(2L)
        );
    }

    @Test
    public void testGetRequestsForEvent() {
        assertThrows(NoSuchElementException.class, () -> privateRequestService.getRequestsForEvent(1L, 1L));

        adminUserService.addUser(testUserRequestDtos[0]);
        assertThrows(NoSuchElementException.class, () -> privateRequestService.getRequestsForEvent(1L, 1L));

        adminUserService.addUser(testUserRequestDtos[1]);
        adminCategoryService.addCategory(testCategoryRequestDtos[1]);
        privateEventSerivce.addEvent(1L, testEventRequestDto);
        assertThrows(DataIntegrityViolationException.class, () -> privateRequestService.getRequestsForEvent(2L, 1L));

        adminUserService.addUser(testUserRequestDtos[2]);
        adminEventService.updateEvent(1L, testEventAdminUpdateRequestDtos[1]);
        privateRequestService.addRequest(2L, 1L);
        privateRequestService.addRequest(3L, 1L);
        assertEquals(
                List.of(testEventRequestResponseDtos[3], testEventRequestResponseDtos[4]),
                privateRequestService.getRequestsForEvent(1L, 1L)
        );
    }

    @Test
    public void testModerateRequests() {
        assertThrows(
                NoSuchElementException.class,
                () -> privateRequestService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        adminUserService.addUser(testUserRequestDtos[0]);
        assertThrows(
                NoSuchElementException.class,
                () -> privateRequestService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        adminCategoryService.addCategory(testCategoryRequestDtos[1]);
        privateEventSerivce.addEvent(1L, testEventRequestDto);
        adminUserService.addUser(testUserRequestDtos[1]);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateRequestService.moderateRequests(2L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        assertThrows(
                NoSuchElementException.class,
                () -> privateRequestService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        adminEventService.updateEvent(1L, testEventAdminUpdateRequestDtos[2]);
        privateRequestService.addRequest(2L, 1L);
        privateEventSerivce.addEvent(1L, testEventRequestDto);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateRequestService.moderateRequests(1L, 2L, testEventRequestUpdateRequestDtos[0])
        );

        privateRequestService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0]);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> privateRequestService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[0])
        );

        adminUserService.addUser(testUserRequestDtos[2]);
        adminUserService.addUser(testUserRequestDtos[3]);
        privateRequestService.addRequest(3L, 1L);
        privateRequestService.addRequest(4L, 1L);
        assertEquals(
                testEventRequestUpdateResponseDto,
                privateRequestService.moderateRequests(1L, 1L, testEventRequestUpdateRequestDtos[1])
        );
    }

    @Test
    public void testCancelRequest() {
        assertThrows(NoSuchElementException.class, () -> privateRequestService.cancelRequest(1L, 1L));

        adminUserService.addUser(testUserRequestDtos[0]);
        assertThrows(NoSuchElementException.class, () -> privateRequestService.cancelRequest(1L, 1L));

        adminCategoryService.addCategory(testCategoryRequestDtos[1]);
        privateEventSerivce.addEvent(1L, testEventRequestDto);
        adminUserService.addUser(testUserRequestDtos[1]);
        adminEventService.updateEvent(1L, testEventAdminUpdateRequestDtos[2]);
        privateRequestService.addRequest(2L, 1L);
        assertThrows(DataIntegrityViolationException.class, () -> privateRequestService.cancelRequest(1L, 1L));

        privateRequestService.cancelRequest(2L, 1L);
        assertThrows(DataIntegrityViolationException.class, () -> privateRequestService.cancelRequest(2L, 1L));

        adminUserService.addUser(testUserRequestDtos[2]);
        privateRequestService.addRequest(3L, 1L);
        assertEquals(
                testEventRequestResponseDtos[7],
                privateRequestService.cancelRequest(3L, 2L)
        );
    }
}
