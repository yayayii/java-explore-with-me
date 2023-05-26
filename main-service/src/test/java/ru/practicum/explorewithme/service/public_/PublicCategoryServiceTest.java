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
import ru.practicum.explorewithme.service.admin.AdminCategoryService;

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
public class PublicCategoryServiceTest {
    private final AdminCategoryService adminCategoryService;
    private final PublicCategoryService publicService;

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


    @Test
    public void testGetCategoryById() {
        assertThrows(NoSuchElementException.class, () -> publicService.getCategoryById(1L));

        adminCategoryService.addCategory(testCategoryRequestDtos[0]);
        assertEquals(testCategoryResponseDtos[0], publicService.getCategoryById(1L));
    }

    @Test
    public void testGetCategories() {
        adminCategoryService.addCategory(testCategoryRequestDtos[0]);
        adminCategoryService.addCategory(testCategoryRequestDtos[1]);
        assertEquals(List.of(testCategoryResponseDtos[1]), publicService.getCategories(1, 1));
    }
}
