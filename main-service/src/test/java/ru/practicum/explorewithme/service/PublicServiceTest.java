package ru.practicum.explorewithme.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;

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
public class PublicServiceTest {
    private final EntityManager entityManager;
    private final AdminService adminService;
    private final PublicService publicService;
    private final PrivateService privateService;

    private static CategoryRequestDto[] testCategoryRequestDtos;
    private static CategoryResponseDto[] testCategoryResponseDtos;
    private static UserResponseDto testUserResponseDto;
    private static UserRequestDto testUserRequestDto;
    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto[] testEventRequestDtos;
    private static EventShortResponseDto[] testEventShortResponseDtos;
    private static CompilationRequestDto[] testCompilationRequestDtos;
    private static CompilationResponseDto[] testCompilationResponseDtos;


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

        testUserRequestDto = new UserRequestDto("email1@yandex.ru", "name1");
        testUserResponseDto = new UserResponseDto(1L, "email1@yandex.ru", "name1");

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
        testEventShortResponseDtos = new EventShortResponseDto[]{
                new EventShortResponseDto(
                        1L, "title1", "annotation1", false, testCategoryResponseDtos[0],
                        0, testLocalDateTime, 0, testUserResponseDto
                ),
                new EventShortResponseDto(
                        2L, "title2", "annotation2", false, testCategoryResponseDtos[0],
                        0, testLocalDateTime, 0, testUserResponseDto
                ),
        };

        testCompilationRequestDtos = new CompilationRequestDto[]{
                new CompilationRequestDto("title1", false, new Long[]{1L, 2L}),
                new CompilationRequestDto("newTitle1", false, new Long[]{2L})
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

    @AfterEach
    public void afterEach() {
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
    public void testGetCategoryById() {
        assertThrows(NoSuchElementException.class, () -> publicService.getCategoryById(1L));

        adminService.addCategory(testCategoryRequestDtos[0]);
        assertEquals(testCategoryResponseDtos[0], publicService.getCategoryById(1L));
    }

    @Test
    public void testGetCategories() {
        adminService.addCategory(testCategoryRequestDtos[0]);
        adminService.addCategory(testCategoryRequestDtos[1]);
        assertEquals(List.of(testCategoryResponseDtos[1]), publicService.getCategories(1, 1));
    }

    //compilations
    @Test
    public void testGetCompilationById() {
        assertThrows(NoSuchElementException.class, () -> publicService.getCompilationById(1L));

        adminService.addCategory(testCategoryRequestDtos[0]);
        adminService.addUser(testUserRequestDto);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        adminService.addCompilation(testCompilationRequestDtos[0]);
        assertEquals(testCompilationResponseDtos[0], publicService.getCompilationById(1L));
    }

    @Test
    public void testGetCompilations() {
        adminService.addCategory(testCategoryRequestDtos[0]);
        adminService.addUser(testUserRequestDto);
        privateService.addEvent(1L, testEventRequestDtos[0]);
        privateService.addEvent(1L, testEventRequestDtos[1]);
        adminService.addCompilation(testCompilationRequestDtos[0]);
        adminService.addCompilation(testCompilationRequestDtos[1]);
        assertEquals(
                List.of(testCompilationResponseDtos[1]),
                publicService.getCompilations(false, 1, 1)
        );
    }
}
