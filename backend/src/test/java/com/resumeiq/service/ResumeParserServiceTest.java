package com.resumeiq.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ResumeParserServiceTest {
    private final ResumeParserService service = new ResumeParserService();

    @Test
    void parseDocxReturnsTextFromDocument() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            document.createParagraph().createRun().setText("Test Resume Content");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);

            MultipartFile file = new MockMultipartFile(
                    "resume",
                    "resume.docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    outputStream.toByteArray()
            );

            String result = service.parse(file);
            assertTrue(result.contains("Test Resume Content"));
        }
    }

    @Test
    void parseUnsupportedExtensionThrows() {
        MultipartFile file = new MockMultipartFile(
                "resume",
                "resume.txt",
                "text/plain",
                "Not a resume".getBytes(StandardCharsets.UTF_8)
        );

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> service.parse(file));
        assertEquals("Only PDF and DOCX resumes are supported", thrown.getMessage());
    }
}
