package ru.practicum.explorewithme.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.categories.CategoryRequestDto;
import ru.practicum.explorewithme.dto.categories.CategoryResponseDto;

import javax.persistence.EntityManager;
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

    private static CategoryRequestDto[] testCategoryRequestDtos;
    private static CategoryResponseDto[] testCategoryResponseDtos;


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
    }

    @AfterEach
    public void afterEach() {
        entityManager.createNativeQuery(
            "delete from category; " +
            "alter table category " +
            "   alter column id " +
            "       restart with 1;"
        ).executeUpdate();
    }


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
}