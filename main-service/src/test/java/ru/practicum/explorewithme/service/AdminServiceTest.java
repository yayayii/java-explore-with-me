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
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;

import javax.persistence.EntityManager;

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


    private static CategoryRequestDto[] testCategoryRequestDtos;
    private static CategoryResponseDto[] testCategoryResponseDtos;
    private static UserRequestDto[] testUserRequestDtos;
    private static UserResponseDto[] testUserResponseDtos;


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
    }

    @BeforeEach
    public void beforeEach() {
        entityManager.createNativeQuery(
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