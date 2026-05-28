package ru.axenix.smartax.dui.service.mcp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertThrows(IllegalStateException.class, tools::listDocumentationFiles);
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

        assertThrows(IllegalArgumentException.class, () -> tools.getDocumentationFile("../secret.txt"));
    }

    @Test
    void getDocumentationFile_ThrowsOnEmptyFileName() {
        DocumentationMcpTool tools = new DocumentationMcpTool();
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.toString());

        assertThrows(IllegalArgumentException.class, () -> tools.getDocumentationFile("  "));
    }

    @Test
    void listDocumentationFiles_FallbackToClasspathWhenFileSystemMissing() {
        DocumentationMcpTool tools = new DocumentationMcpTool();
        ReflectionTestUtils.setField(tools, "documentationPath", "mcp-tools");

        List<String> files = tools.listDocumentationFiles();

        assertTrue(files.contains("listCollections.md"));
    }

    @Test
    void getDocumentationFile_FallbackToClasspathWhenFileSystemMissing() {
        DocumentationMcpTool tools = new DocumentationMcpTool();
        ReflectionTestUtils.setField(tools, "documentationPath", "mcp-tools");

        String content = tools.getDocumentationFile("listCollections.md");

        assertTrue(content.contains("listCollections"));
    }

    @Test
    void getDocumentationFile_ThrowsWhenFileNotFoundInAnySource() {
        DocumentationMcpTool tools = new DocumentationMcpTool();
        ReflectionTestUtils.setField(tools, "documentationPath", tempDir.resolve("missing").toString());

        assertThrows(IllegalArgumentException.class, () -> tools.getDocumentationFile("absent.md"));
    }

}