package ru.axenix.smartax.dui.service.application.page.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.contract.model.PageShortDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(pageController).build();
    }

    @Test
    void testGetPageByName() throws Exception {
        String pageName = "testPage";

        PageDto pageDto = new PageDto();
        pageDto.setName(pageName);

        when(pageService.getPageByName(pageName)).thenReturn(pageDto);

        mockMvc.perform(get("/app/v1/page")
                        .param("pageName", pageName))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(pageName));
    }

    @Test
    void testGetPageById() throws Exception {
        UUID pageId = UUID.randomUUID();

        PageDto pageDto = new PageDto();
        pageDto.setId(pageId);
        pageDto.setName("testPage");

        when(pageService.getPage(pageId)).thenReturn(pageDto);

        mockMvc.perform(get("/app/v1/page/{pageUUID}", pageId))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(pageId.toString()))
            .andExpect(jsonPath("$.name").value("testPage"));

    }

    @Test
    void testGetAllPages() throws Exception {
        UUID pageId = UUID.randomUUID();
        when(pageService.listPages()).thenReturn(List.of(new PageShortDto(pageId, "dashboard", "Dashboard")));

        mockMvc.perform(post("/app/v1/page/list"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(pageId.toString()))
            .andExpect(jsonPath("$[0].name").value("dashboard"))
            .andExpect(jsonPath("$[0].title").value("Dashboard"));
    }

    @Test
    void testCreatePage() throws Exception {
        PageDto request = new PageDto();
        request.setName("createdPage");
        request.setTitle("Created");
        request.setPages(List.of(Map.of("k", "v")));
        request.setMocks(List.of(Map.of("m", "1")));

        PageDto response = new PageDto();
        response.setName("createdPage");
        response.setTitle("Created");
        response.setPages(List.of(Map.of("k", "v")));
        response.setMocks(List.of(Map.of("m", "1")));

        when(pageService.createPage(any(PageDto.class))).thenReturn(response);

        mockMvc.perform(post("/app/v1/page")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("createdPage"))
            .andExpect(jsonPath("$.pages[0].k").value("v"))
            .andExpect(jsonPath("$.mocks[0].m").value("1"));
    }

    @Test
    void testEditPage() throws Exception {
        PageDto request = new PageDto();
        request.setName("editedPage");
        request.setTitle("Edited");
        request.setPages(List.of(Map.of("k", "new")));
        request.setMocks(List.of(Map.of("m", "2")));

        PageDto response = new PageDto();
        response.setName("editedPage");
        response.setTitle("Edited");
        response.setPages(List.of(Map.of("k", "new")));
        response.setMocks(List.of(Map.of("m", "2")));

        UUID pageId = UUID.randomUUID();

        when(pageService.editPage(eq(pageId), any(PageDto.class))).thenReturn(response);

        mockMvc.perform(put("/app/v1/page/{pageUUID}", pageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("editedPage"))
            .andExpect(jsonPath("$.pages[0].k").value("new"))
            .andExpect(jsonPath("$.mocks[0].m").value("2"));
    }
}