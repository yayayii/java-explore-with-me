package ru.practicum.explorewithme.service.admin;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
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
public class AdminUserServiceTest {
    private final EntityManager entityManager;
    private final AdminUserService adminService;

    private static UserRequestDto[] testUserRequestDtos;
    private static UserResponseDto[] testUserResponseDtos;


    @BeforeAll
    public static void beforeAll() {
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
        assertEquals(List.of(testUserResponseDtos[1]), adminService.getUsers(null, 1, 1));
    }

    @Test
    public void testDeleteUser() {
        assertThrows(NoSuchElementException.class, () -> adminService.deleteUser(1L));

        adminService.addUser(testUserRequestDtos[0]);
        assertDoesNotThrow(() -> adminService.deleteUser(1L));
        assertThrows(NoSuchElementException.class, () -> adminService.deleteUser(1L));
    }
}
