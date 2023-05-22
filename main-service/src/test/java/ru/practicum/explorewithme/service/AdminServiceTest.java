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
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.EventState;
import ru.practicum.explorewithme.model.event.EventUpdateState;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Collections;
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
public class AdminServiceTest {
    private final EntityManager entityManager;
    private final AdminService adminService;
    private final PrivateService privateService;


    private static CategoryRequestDto[] testCategoryRequestDtos;
    private static CategoryResponseDto[] testCategoryResponseDtos;
    private static CompilationRequestDto[] testCompilationRequestDtos;
    private static CompilationResponseDto[] testCompilationResponseDtos;
    private static UserRequestDto[] testUserRequestDtos;
    private static UserResponseDto[] testUserResponseDtos;
    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto[] testEventRequestDtos;
    private static EventUpdateRequestDto testEventUpdateRequestDto;
    private static EventShortResponseDto[] testEventShortResponseDtos;
    private static EventResponseDto[] testEventResponseDtos;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDtos = new CategoryRequestDto[]{
                new CategoryRequestDto("name1"),
                new CategoryRequestDto("name2")
        };
        testCategoryResponseDtos = new CategoryResponseDto[]{
                new CategoryResponseDto(1L, "name1"),
                new CategoryResponseDto(2L, "name2")
        };

        testUserRequestDtos = new UserRequestDto[]{
                new UserRequestDto("email1@yandex.ru", "name1"),
                new UserRequestDto("email2@yandex.ru", "name2")
        };
        testUserResponseDtos = new UserResponseDto[]{
                new UserResponseDto(1L, "email1@yandex.ru", "name1"),
                new UserResponseDto(2L, "email2@yandex.ru", "name2")
        };

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
        testEventUpdateRequestDto = new EventUpdateRequestDto(
                "newTitle1", "newAnnotation1", "newDescription1",
                true, true, 2, testLocalDateTime,
                new LocationDto(0.0, 0.0), 1L, EventUpdateState.PUBLISH_EVENT
        );
        testEventShortResponseDtos = new EventShortResponseDto[]{
                new EventShortResponseDto(
                        1L, "title1", "annotation1", false, testCategoryResponseDtos[0],
                        0, testLocalDateTime, 0, testUserResponseDtos[0]
                ),
                new EventShortResponseDto(
                        2L, "title2", "annotation2", false, testCategoryResponseDtos[0],
                        0, testLocalDateTime, 0, testUserResponseDtos[0]
                ),
        };
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
                    new UserResponseDto(1L, "email1@yandex.ru", "name1"), EventState.PUBLISHED
                )
        };

        testCompilationRequestDtos = new CompilationRequestDto[]{
                new CompilationRequestDto("title1", false, new Long[]{1L, 2L}),
                new CompilationRequestDto("newTitle1", true, new Long[]{2L})
        };
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

    @BeforeEach
    public void beforeEach() {
        entityManager.createNativeQuery(
            "delete from event_compilation; " +
            "delete from compilation; " +
            "alter table compilation " +
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


    //categories
    @Test
    public void testAddCategory() {
        assertEquals(testCategoryResponseDtos[0], adminService.addCategory(testCategoryRequestDtos[0]));

        assertThrows(DataIntegrityViolationException.class, () -> adminService.addCategory(testCategoryRequestDtos[0]));
    }

    @Test
    public void testUpdateCategory() {
        assertThrows(
                NoSuchElementException.class,
                () -> adminService.updateCategory(1L, testCategoryRequestDtos[0])
        );

        adminService.addCategory(testCategoryRequestDtos[0]);
        assertEquals(
                new CategoryResponseDto(1L, "name2"),
                adminService.updateCategory(1L, testCategoryRequestDtos[1])
        );
    }

    @Test
    public void testDeleteCategory() {
        assertThrows(NoSuchElementException.class, () -> adminService.deleteCategory(1L));

        adminService.addCategory(testCategoryRequestDtos[0]);
        assertDoesNotThrow(() -> adminService.deleteCategory(1L));
        assertThrows(NoSuchElementException.class, () -> adminService.deleteCategory(1L));
    }

    //compilations
    @Test
    public void testAddCompilation() {
        assertThrows(NoSuchElementException.class, () -> adminService.addCompilation(testCompilationRequestDtos[0]));

        adminService.addCategory(testCategoryRequestDtos[0]);
        adminService.addUser(testUserRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        assertEquals(testCompilationResponseDtos[0], adminService.addCompilation(testCompilationRequestDtos[0]));
    }

    @Test
    public void testUpdateCompilation() {
        assertThrows(
                NoSuchElementException.class,
                () -> adminService.updateCompilation(1L, testCompilationRequestDtos[0])
        );

        adminService.addCategory(testCategoryRequestDtos[0]);
        adminService.addUser(testUserRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        adminService.addCompilation(testCompilationRequestDtos[0]);
        testCompilationRequestDtos[1].setEvents(new Long[]{3L});
        assertThrows(
                NoSuchElementException.class,
                () -> adminService.updateCompilation(1L, testCompilationRequestDtos[1])
        );
        testCompilationRequestDtos[1].setEvents(new Long[]{2L});

        assertEquals(
                testCompilationResponseDtos[1],
                adminService.updateCompilation(1L, testCompilationRequestDtos[1])
        );
    }

    @Test
    public void testDeleteCompilation() {
        assertThrows(NoSuchElementException.class, () -> adminService.deleteCompilation(1L));

        adminService.addCategory(testCategoryRequestDtos[0]);
        adminService.addUser(testUserRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        adminService.addCompilation(testCompilationRequestDtos[0]);
        assertDoesNotThrow(() -> adminService.deleteCompilation(1L));
        assertThrows(NoSuchElementException.class, () -> adminService.deleteCompilation(1L));
    }

    //events
    @Test
    public void testSearchEvents() {
        adminService.addUser(testUserRequestDtos[0]);
        adminService.addCategory(testCategoryRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[0]);

        assertEquals(Collections.emptyList(), adminService.searchEvents(
                List.of(2L), List.of(EventState.PENDING), List.of(1L),
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), 0, 10));
        assertEquals(Collections.emptyList(), adminService.searchEvents(
                List.of(1L), List.of(EventState.PUBLISHED), List.of(1L),
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), 0, 10));
        assertEquals(Collections.emptyList(), adminService.searchEvents(
                List.of(1L), List.of(EventState.PENDING), List.of(2L),
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), 0, 10));
        assertEquals(Collections.emptyList(), adminService.searchEvents(
                List.of(1L), List.of(EventState.PENDING), List.of(1L),
                testLocalDateTime.plusDays(1), testLocalDateTime.plusDays(2), 0, 10));
        assertEquals(Collections.emptyList(), adminService.searchEvents(
                List.of(1L), List.of(EventState.PENDING), List.of(1L),
                testLocalDateTime.minusDays(2), testLocalDateTime.minusDays(1), 0, 10));

        assertEquals(List.of(testEventResponseDtos[0]), adminService.searchEvents(
                List.of(1L), List.of(EventState.PENDING), List.of(1L),
                testLocalDateTime.minusDays(1), testLocalDateTime.plusDays(1), 0, 10));
    }

    @Test
    public void testUpdateAdminEvent() {
        assertThrows(
                NoSuchElementException.class,
                () -> adminService.updateEvent(1L, testEventUpdateRequestDto)
        );
        adminService.addUser(testUserRequestDtos[0]);
        adminService.addCategory(testCategoryRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[0]);

        testEventUpdateRequestDto.setEventDate(LocalDateTime.now());
        assertThrows(
                DataIntegrityViolationException.class,
                () -> adminService.updateEvent(1L, testEventUpdateRequestDto)
        );
        testEventUpdateRequestDto.setEventDate(testLocalDateTime);

        testEventUpdateRequestDto.setCategory(2L);
        assertThrows(
                NoSuchElementException.class,
                () -> adminService.updateEvent(1L, testEventUpdateRequestDto)
        );
        testEventUpdateRequestDto.setCategory(1L);

        assertEquals(testEventResponseDtos[1], adminService.updateEvent(1L, testEventUpdateRequestDto));
    }

    //users
    @Test
    public void testAddUser() {
        assertEquals(testUserResponseDtos[0], adminService.addUser(testUserRequestDtos[0]));

        assertThrows(DataIntegrityViolationException.class, () -> adminService.addUser(testUserRequestDtos[0]));
    }

    @Test
    public void testGetUsers() {
        adminService.addUser(testUserRequestDtos[0]);
        adminService.addUser(testUserRequestDtos[1]);
        assertEquals(List.of(testUserResponseDtos[1]), adminService.getUsers(1, 1));
    }

    @Test
    public void testDeleteUser() {
        assertThrows(NoSuchElementException.class, () -> adminService.deleteUser(1L));

        adminService.addUser(testUserRequestDtos[0]);
        assertDoesNotThrow(() -> adminService.deleteUser(1L));
        assertThrows(NoSuchElementException.class, () -> adminService.deleteUser(1L));
    }
}