package ru.practicum.explorewithme.service.admin;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;

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
public class AdminUserServiceTest {
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
