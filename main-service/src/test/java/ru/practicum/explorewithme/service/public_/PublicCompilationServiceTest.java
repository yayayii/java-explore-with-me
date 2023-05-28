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
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.admin.AdminCategoryService;
import ru.practicum.explorewithme.service.admin.AdminCompilationService;
import ru.practicum.explorewithme.service.admin.AdminEventService;
import ru.practicum.explorewithme.service.admin.AdminUserService;
import ru.practicum.explorewithme.service.private_.PrivateEventService;

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
public class PublicCompilationServiceTest {
    private final AdminCategoryService adminCategoryService;
    private final AdminCompilationService adminCompilationService;
    private final AdminEventService adminEventService;
    private final AdminUserService adminUserService;
    private final PrivateEventService privateService;
    private final PublicCompilationService publicService;

    private static CategoryRequestDto testCategoryRequestDto;
    private static UserRequestDto testUserRequestDto;
    private static EventRequestDto[] testEventRequestDtos;
    private static EventUpdateRequestDto[] testEventUpdateRequestDtos;
    private static CompilationRequestDto[] testCompilationRequestDtos;
    private static CompilationResponseDto[] testCompilationResponseDtos;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDto = new CategoryRequestDto("name1");
        CategoryResponseDto testCategoryResponseDto = new CategoryResponseDto(1L, "name1");

        testUserRequestDto = new UserRequestDto("name1", "email1@yandex.ru");
        UserResponseDto testUserResponseDto = new UserResponseDto(1L, "name1", "email1@yandex.ru");

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
                )
        };

        testCompilationRequestDtos = new CompilationRequestDto[]{
                new CompilationRequestDto("title1", false, List.of(1L, 2L)),
                new CompilationRequestDto("newTitle1", false, List.of(2L))
        };
        testCompilationResponseDtos = new CompilationResponseDto[]{
                new CompilationResponseDto(
                        1L, "title1", false,
                        List.of(testEventShortResponseDtos[0], testEventShortResponseDtos[1])
                ),
                new CompilationResponseDto(
                        2L, "newTitle1", false,
                        List.of(testEventShortResponseDtos[1])
                ),
        };
    }


    @Test
    public void testGetCompilationById() {
        assertThrows(NoSuchElementException.class, () -> publicService.getCompilationById(1L));

        adminCategoryService.addCategory(testCategoryRequestDto);
        adminUserService.addUser(testUserRequestDto);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        adminEventService.updateEvent(1L, testEventUpdateRequestDtos[0]);
        adminEventService.updateEvent(2L, testEventUpdateRequestDtos[1]);
        adminCompilationService.addCompilation(testCompilationRequestDtos[0]);
        assertEquals(testCompilationResponseDtos[0], publicService.getCompilationById(1L));
    }

    @Test
    public void testGetCompilations() {
        adminCategoryService.addCategory(testCategoryRequestDto);
        adminUserService.addUser(testUserRequestDto);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        adminEventService.updateEvent(1L, testEventUpdateRequestDtos[0]);
        adminEventService.updateEvent(2L, testEventUpdateRequestDtos[1]);
        adminCompilationService.addCompilation(testCompilationRequestDtos[0]);
        adminCompilationService.addCompilation(testCompilationRequestDtos[1]);
        assertEquals(
                List.of(testCompilationResponseDtos[1]),
                publicService.getCompilations(false, 1, 1)
        );
    }
}
