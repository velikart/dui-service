package ru.axenix.smartax.dui.service.mcp.tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import ru.axenix.smartax.common.exception.SmartaxException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentationMcpToolTest {

    @TempDir
    private Path tempDir;

    @Test
    void listDocumentationFiles_ReturnsSortedRegularFiles() throws Exception {
        DocumentationMcpTool tools = new DocumentationMcpTool();
        Files.writeString(tempDir.resolve("b.md"), "b");
        Files.writeString(tempDir.resolve("a.md"), "a");
        Files.createDirectories(tempDir.resolve("folder"));
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.toString());

        List<String> files = tools.listDocumentationFiles();

        assertEquals(List.of("a.md", "b.md"), files);
    }

    @Test
    void listDocumentationFiles_ThrowsWhenFolderNotExists() {
        DocumentationMcpTool tools = new DocumentationMcpTool();
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.resolve("missing").toString());

        assertThrows(SmartaxException.class, tools::listDocumentationFiles);
    }

    @Test
    void getDocumentationFile_ReturnsContent() throws Exception {
        DocumentationMcpTool tools = new DocumentationMcpTool();
        Files.writeString(tempDir.resolve("doc.md"), "hello");
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.toString());

        String content = tools.getDocumentationFile("doc.md");

        assertEquals("hello", content);
    }

    @Test
    void getDocumentationFile_ThrowsOnTraversal() {
        DocumentationMcpTool tools = new DocumentationMcpTool();
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.toString());

        assertThrows(SmartaxException.class, () -> tools.getDocumentationFile("../secret.txt"));
    }

    @Test
    void getDocumentationFile_ThrowsOnEmptyFileName() {
        DocumentationMcpTool tools = new DocumentationMcpTool();
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.toString());

        assertThrows(SmartaxException.class, () -> tools.getDocumentationFile("  "));
    }
}