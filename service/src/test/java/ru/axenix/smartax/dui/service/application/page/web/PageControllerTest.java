package ru.axenix.smartax.dui.service.application.page.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.application.page.model.PageDto;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PageControllerTest {

    @Mock
    private PageService pageService;

    @InjectMocks
    private PageController pageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(pageController).build();
    }

    @Test
    void testGetPageByName() throws Exception {
        // Arrange
        String pageName = "testPage";
        PageDto pageDto = new PageDto();
        pageDto.setName(pageName);
        when(pageService.getPageByName(pageName)).thenReturn(pageDto);

        mockMvc.perform(get("/app/v1/page").param("pageName", pageName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(pageName));
    }

    @Test
    void testGetPageInstructions() throws Exception {
        // Arrange
        String pageName = "testPage";
        PageDto pageDto = new PageDto();
        pageDto.setName(pageName);
        pageDto.setInstructions("Test instructions");
        when(pageService.getPageByName(pageName)).thenReturn(pageDto);

        mockMvc.perform(get("/app/v1/page/instructions").param("pageName", pageName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Test instructions"));
    }
}
