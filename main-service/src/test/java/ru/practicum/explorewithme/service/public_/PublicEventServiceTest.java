package ru.practicum.explorewithme.service.public_;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.service.admin.AdminCategoryService;
import ru.practicum.explorewithme.service.admin.AdminEventService;
import ru.practicum.explorewithme.service.admin.AdminUserService;
import ru.practicum.explorewithme.service.private_.PrivateEventService;

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
public class PublicEventServiceTest {
    private final AdminCategoryService adminCategoryService;
    private final AdminEventService adminEventService;
    private final AdminUserService adminUserService;
    private final PublicEventService publicService;
    private final PrivateEventService privateService;

    private static CategoryRequestDto testCategoryRequestDto;
    private static UserRequestDto testUserRequestDto;
    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto[] testEventRequestDtos;
    private static EventUpdateRequestDto[] testEventUpdateRequestDtos;
    private static EventShortResponseDto testEventShortResponseDto;
    private static EventResponseDto testEventResponseDto;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDto = new CategoryRequestDto("name1");
        CategoryResponseDto testCategoryResponseDto = new CategoryResponseDto(1L, "name1");

        testUserRequestDto = new UserRequestDto("name1", "email1@yandex.ru");
        UserResponseDto testUserResponseDto = new UserResponseDto(1L, "name1", "email1@yandex.ru");

        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventRequestDtos = new EventRequestDto[]{
                new EventRequestDto(
                        "title1", "annotation1", "description1", false,
                        false, 1, testLocalDateTime,
                        new LocationDto(1.1, 1.1), 1L
                ),
                new EventRequestDto(
                        "title2", "annotation2", "description2", false,
                        false, 1, testLocalDateTime,
                        new LocationDto(1.1, 1.1), 1L
                ),
        };
        testEventUpdateRequestDtos = new EventUpdateRequestDto[]{
                new EventUpdateRequestDto(
                        "title1", "annotation1", "description1",
                        false, false, 1, testLocalDateTime,
                        new LocationDto(1.1, 1.1), 1L, EventUpdateState.PUBLISH_EVENT
                ),
                new EventUpdateRequestDto(
                        "title2", "annotation2", "description2",
                        false, false, 1, testLocalDateTime,
                        new LocationDto(1.1, 1.1), 1L, EventUpdateState.PUBLISH_EVENT
                )
        };
        testEventShortResponseDto = new EventShortResponseDto(
                2L, "title2", "annotation2", false, testCategoryResponseDto,
                0, testLocalDateTime, 0, testUserResponseDto,
                EventState.PUBLISHED, testLocalDateTime
        );
        testEventResponseDto = new EventResponseDto(
                1L, "title1", "annotation1", "description1", false,
                false, testCategoryResponseDto, 1, 0,
                testLocalDateTime, testLocalDateTime, testLocalDateTime, new LocationDto(1.1, 1.1), 0,
                testUserResponseDto, EventState.PUBLISHED, Collections.emptyList()
        );
    }


    @Test
    public void testGetEventById() {
        assertThrows(NoSuchElementException.class, () -> publicService.getEventById(1L));

        adminCategoryService.addCategory(testCategoryRequestDto);
        adminUserService.addUser(testUserRequestDto);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        assertThrows(NoSuchElementException.class, () -> publicService.getEventById(1L));

        adminEventService.updateEvent(1L, testEventUpdateRequestDtos[0]);
        assertEquals(testEventResponseDto, publicService.getEventById(1L));
    }

    @Test
    public void testGetEvents() {
        adminCategoryService.addCategory(testCategoryRequestDto);
        adminUserService.addUser(testUserRequestDto);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        assertEquals(
                Collections.emptyList(),
                publicService.getEvents(
                    "description", List.of(1L, 2L), false, testLocalDateTime.minusDays(1),
                    testLocalDateTime.plusDays(1), false, 1, 1
                )
        );

        adminEventService.updateEvent(1L, testEventUpdateRequestDtos[0]);
        adminEventService.updateEvent(2L, testEventUpdateRequestDtos[1]);
        assertEquals(
                List.of(testEventShortResponseDto),
                publicService.getEvents(
                        "description", List.of(1L, 2L), false, testLocalDateTime.minusDays(1),
                        testLocalDateTime.plusDays(1), false, 1, 1
                )
        );
    }
}
