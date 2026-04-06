package ru.axenix.smartax.dui.service.mcp.tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentationMcpToolsTest {

    @TempDir
    private Path tempDir;

    @Test
    void listDocumentationFiles_ReturnsSortedRegularFiles() throws Exception {
        DocumentationMcpTools tools = new DocumentationMcpTools();
        Files.writeString(tempDir.resolve("b.md"), "b");
        Files.writeString(tempDir.resolve("a.md"), "a");
        Files.createDirectories(tempDir.resolve("folder"));
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.toString());

        List<String> files = tools.listDocumentationFiles();

        assertEquals(List.of("a.md", "b.md"), files);
    }

    @Test
    void listDocumentationFiles_ThrowsWhenFolderNotExists() {
        DocumentationMcpTools tools = new DocumentationMcpTools();
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.resolve("missing").toString());

        assertThrows(IllegalStateException.class, tools::listDocumentationFiles);
    }

    @Test
    void getDocumentationFile_ReturnsContent() throws Exception {
        DocumentationMcpTools tools = new DocumentationMcpTools();
        Files.writeString(tempDir.resolve("doc.md"), "hello");
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.toString());

        String content = tools.getDocumentationFile("doc.md");

        assertEquals("hello", content);
    }

    @Test
    void getDocumentationFile_ThrowsOnTraversal() {
        DocumentationMcpTools tools = new DocumentationMcpTools();
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.toString());

        assertThrows(IllegalArgumentException.class, () -> tools.getDocumentationFile("../secret.txt"));
    }

    @Test
    void getDocumentationFile_ThrowsOnEmptyFileName() {
        DocumentationMcpTools tools = new DocumentationMcpTools();
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.toString());

        assertThrows(IllegalArgumentException.class, () -> tools.getDocumentationFile("  "));
    }
}
