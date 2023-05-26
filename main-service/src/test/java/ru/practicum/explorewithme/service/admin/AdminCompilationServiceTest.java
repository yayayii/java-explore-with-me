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
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationUpdateRequestDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.private_.PrivateEventSerivce;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

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
public class AdminCompilationServiceTest {
    private final AdminCategoryService adminCategoryService;
    private final AdminCompilationService adminCompilationService;
    private final AdminEventService adminEventService;
    private final AdminUserService adminUserService;
    private final PrivateEventSerivce privateService;


    private static CategoryRequestDto testCategoryRequestDto;
    private static UserRequestDto testUserRequestDto;
    private static EventRequestDto[] testEventRequestDtos;
    private static EventUpdateRequestDto[] testEventUpdateRequestDtos;
    private static CompilationRequestDto testCompilationRequestDto;
    private static CompilationUpdateRequestDto testCompilationUpdateRequestDto;
    private static CompilationResponseDto[] testCompilationResponseDtos;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDto = new CategoryRequestDto("name1");
        CategoryResponseDto testCategoryResponseDto = new CategoryResponseDto(1L, "name1");

        testUserRequestDto = new UserRequestDto("email1@yandex.ru", "name1");
        UserResponseDto testUserResponseDto = new UserResponseDto(1L, "email1@yandex.ru", "name1");

        LocalDateTime testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
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
                        "newTitle1", "newAnnotation1", "newDescription1",
                        true, true, 2, testLocalDateTime,
                        new LocationDto(0.0, 0.0), 1L, EventUpdateState.PUBLISH_EVENT
                ),
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
        EventShortResponseDto[] testEventShortResponseDtos = new EventShortResponseDto[]{
                new EventShortResponseDto(
                        1L, "title1", "annotation1", false, testCategoryResponseDto,
                        0, testLocalDateTime, 0, testUserResponseDto,
                        EventState.PUBLISHED, testLocalDateTime
                ),
                new EventShortResponseDto(
                        2L, "title2", "annotation2", false, testCategoryResponseDto,
                        0, testLocalDateTime, 0, testUserResponseDto,
                        EventState.PUBLISHED, testLocalDateTime
                ),
        };

        testCompilationRequestDto = new CompilationRequestDto("title1", false, List.of(1L, 2L));
        testCompilationUpdateRequestDto = new CompilationUpdateRequestDto("newTitle1", true, List.of(2L));
        testCompilationResponseDtos = new CompilationResponseDto[]{
                new CompilationResponseDto(
                        1L, "title1", false,
                        List.of(testEventShortResponseDtos[0], testEventShortResponseDtos[1])
                ),
                new CompilationResponseDto(
                        1L, "newTitle1", true,
                        List.of(testEventShortResponseDtos[1])
                ),
        };
    }


    @Test
    public void testAddCompilation() {
        assertThrows(NoSuchElementException.class, () -> adminCompilationService.addCompilation(testCompilationRequestDto));

        adminCategoryService.addCategory(testCategoryRequestDto);
        adminUserService.addUser(testUserRequestDto);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        adminEventService.updateEvent(1L, testEventUpdateRequestDtos[1]);
        adminEventService.updateEvent(2L, testEventUpdateRequestDtos[2]);
        assertEquals(testCompilationResponseDtos[0], adminCompilationService.addCompilation(testCompilationRequestDto));
    }

    @Test
    public void testUpdateCompilation() {
        assertThrows(
                NoSuchElementException.class,
                () -> adminCompilationService.updateCompilation(1L, testCompilationUpdateRequestDto)
        );

        adminCategoryService.addCategory(testCategoryRequestDto);
        adminUserService.addUser(testUserRequestDto);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        adminEventService.updateEvent(1L, testEventUpdateRequestDtos[1]);
        adminEventService.updateEvent(2L, testEventUpdateRequestDtos[2]);
        adminCompilationService.addCompilation(testCompilationRequestDto);
        testCompilationUpdateRequestDto.setEvents(List.of(3L));
        assertThrows(
                NoSuchElementException.class,
                () -> adminCompilationService.updateCompilation(1L, testCompilationUpdateRequestDto)
        );
        testCompilationUpdateRequestDto.setEvents(List.of(2L));

        assertEquals(
                testCompilationResponseDtos[1],
                adminCompilationService.updateCompilation(1L, testCompilationUpdateRequestDto)
        );
    }

    @Test
    public void testDeleteCompilation() {
        assertThrows(NoSuchElementException.class, () -> adminCompilationService.deleteCompilation(1L));

        adminCategoryService.addCategory(testCategoryRequestDto);
        adminUserService.addUser(testUserRequestDto);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        adminEventService.updateEvent(1L, testEventUpdateRequestDtos[1]);
        adminEventService.updateEvent(2L, testEventUpdateRequestDtos[2]);
        adminCompilationService.addCompilation(testCompilationRequestDto);
        assertDoesNotThrow(() -> adminCompilationService.deleteCompilation(1L));
        assertThrows(NoSuchElementException.class, () -> adminCompilationService.deleteCompilation(1L));
    }
}
