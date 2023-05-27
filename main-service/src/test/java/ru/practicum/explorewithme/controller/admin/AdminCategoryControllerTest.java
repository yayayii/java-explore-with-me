package ru.practicum.explorewithme.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.service.admin.AdminCategoryService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminCategoryControllerTest {
    @Mock
    private AdminCategoryService mockAdminService;
    @InjectMocks
    private AdminCategoryController adminController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static final String API_PREFIX = "/admin/categories";
    private static CategoryRequestDto testCategoryRequestDto;
    private static CategoryResponseDto testCategoryResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();

        testCategoryRequestDto = new CategoryRequestDto("name1");
        testCategoryResponseDto = new CategoryResponseDto(1L, "name1");
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }


    @Test
    public void testAddCategory() throws Exception {
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(new CategoryRequestDto("")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        when(mockAdminService.addCategory(any()))
                .thenReturn(testCategoryResponseDto);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testCategoryRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateCategory() throws Exception {
        mockMvc.perform(patch(API_PREFIX + "/1")
                        .content(objectMapper.writeValueAsString(new CategoryRequestDto("")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        when(mockAdminService.updateCategory(anyLong(), any()))
                .thenReturn(testCategoryResponseDto);
        mockMvc.perform(patch(API_PREFIX + "/1")
                        .content(objectMapper.writeValueAsString(testCategoryRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        doNothing().when(mockAdminService).deleteCategory(anyLong());
        mockMvc.perform(delete(API_PREFIX + "/1"))
                .andExpect(status().isNoContent());
    }
}
