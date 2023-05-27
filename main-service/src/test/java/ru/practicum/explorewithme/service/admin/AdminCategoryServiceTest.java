package ru.practicum.explorewithme.service.admin;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;

import javax.transaction.Transactional;
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
public class AdminCategoryServiceTest {
    private final AdminCategoryService adminService;

    private static CategoryRequestDto[] testCategoryRequestDtos;
    private static CategoryResponseDto testCategoryResponseDto;


    @BeforeAll
    public static void beforeAll() {
        testCategoryRequestDtos = new CategoryRequestDto[]{
                new CategoryRequestDto("name1"),
                new CategoryRequestDto("name2")
        };
        testCategoryResponseDto = new CategoryResponseDto(1L, "name1");
    }


    @Test
    public void testAddCategory() {
        assertEquals(testCategoryResponseDto, adminService.addCategory(testCategoryRequestDtos[0]));

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
}
