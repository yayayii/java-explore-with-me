package ru.practicum.explorewithme.service.admin;

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
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.private_.PrivateEventSerivce;

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
public class AdminEventServiceTest {
    private final AdminCategoryService adminCategoryService;
    private final AdminEventService adminEventService;
    private final AdminUserService adminUserService;
    private final PrivateEventSerivce privateService;


    private static CategoryRequestDto testCategoryRequestDto;
    private static UserRequestDto testUserRequestDto;
    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto testEventRequestDto;
    private static EventUpdateRequestDto testEventUpdateRequestDto;
    private static EventResponseDto[] testEventResponseDtos;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDto = new CategoryRequestDto("name1");
        CategoryResponseDto testCategoryResponseDto = new CategoryResponseDto(1L, "name1");

        testUserRequestDto = new UserRequestDto("email1@yandex.ru", "name1");
        UserResponseDto testUserResponseDto = new UserResponseDto(1L, "email1@yandex.ru", "name1");

        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventRequestDto = new EventRequestDto(
                "title1", "annotation1", "description1", false,
                false, 1, testLocalDateTime,
                new LocationDto(1.1, 1.1), 1L
        );
        testEventUpdateRequestDto = new EventUpdateRequestDto(
                "newTitle1", "newAnnotation1", "newDescription1",
                true, true, 2, testLocalDateTime,
                new LocationDto(0.0, 0.0), 1L, EventUpdateState.PUBLISH_EVENT
        );
        testEventResponseDtos = new EventResponseDto[] {
                new EventResponseDto(
                        1L, "title1", "annotation1", "description1", false,
                        false, testCategoryResponseDto, 1, 0,
                        testLocalDateTime, testLocalDateTime, null, new LocationDto(1.1, 1.1),
                        0, testUserResponseDto, EventState.PENDING
                ),
                new EventResponseDto(
                        1L, "newTitle1", "newAnnotation1", "newDescription1", true,
                        true, testCategoryResponseDto, 2, 0,
                        testLocalDateTime, testLocalDateTime, null, new LocationDto(0.0, 0.0),
                        0, testUserResponseDto, EventState.PUBLISHED
                )
        };
    }


    @Test
    public void testSearchEvents() {
        adminUserService.addUser(testUserRequestDto);
        adminCategoryService.addCategory(testCategoryRequestDto);
        privateService.addEvent(1L, testEventRequestDto);

        assertEquals(Collections.emptyList(), adminEventService.searchEvents(
                List.of(2L), List.of(EventState.PENDING), List.of(1L),
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), 0, 10));
        assertEquals(Collections.emptyList(), adminEventService.searchEvents(
                List.of(1L), List.of(EventState.PUBLISHED), List.of(1L),
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), 0, 10));
        assertEquals(Collections.emptyList(), adminEventService.searchEvents(
                List.of(1L), List.of(EventState.PENDING), List.of(2L),
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), 0, 10));
        assertEquals(Collections.emptyList(), adminEventService.searchEvents(
                List.of(1L), List.of(EventState.PENDING), List.of(1L),
                testLocalDateTime.plusDays(1), testLocalDateTime.plusDays(2), 0, 10));
        assertEquals(Collections.emptyList(), adminEventService.searchEvents(
                List.of(1L), List.of(EventState.PENDING), List.of(1L),
                testLocalDateTime.minusDays(2), testLocalDateTime.minusDays(1), 0, 10));

        assertEquals(List.of(testEventResponseDtos[0]), adminEventService.searchEvents(
                List.of(1L), List.of(EventState.PENDING), List.of(1L),
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), 0, 10));
    }

    @Test
    public void testUpdateEvent() {
        assertThrows(
                NoSuchElementException.class,
                () -> adminEventService.updateEvent(1L, testEventUpdateRequestDto)
        );
        adminUserService.addUser(testUserRequestDto);
        adminCategoryService.addCategory(testCategoryRequestDto);
        privateService.addEvent(1L, testEventRequestDto);

        testEventUpdateRequestDto.setEventDate(LocalDateTime.now());
        assertThrows(
                IllegalArgumentException.class,
                () -> adminEventService.updateEvent(1L, testEventUpdateRequestDto)
        );
        testEventUpdateRequestDto.setEventDate(testLocalDateTime);

        testEventUpdateRequestDto.setCategory(2L);
        assertThrows(
                NoSuchElementException.class,
                () -> adminEventService.updateEvent(1L, testEventUpdateRequestDto)
        );
        testEventUpdateRequestDto.setCategory(1L);

        assertEquals(testEventResponseDtos[1], adminEventService.updateEvent(1L, testEventUpdateRequestDto));
    }
}
