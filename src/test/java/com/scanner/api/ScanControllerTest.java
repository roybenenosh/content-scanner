package com.scanner.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScanController.class)
@Import(ScannerConfig.class)
class ScanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ResultActions postScan(String body) throws Exception {
        return mockMvc.perform(post("/scan").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void shouldReturnAllFindingsForSpecExample() throws Exception {
        postScan("""
                {"content": "banana", "signatures": ["ana", "na"]}
                """)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {"findings": [
                          {"signature": "ana", "startIndex": 1, "endIndex": 4},
                          {"signature": "na",  "startIndex": 2, "endIndex": 4},
                          {"signature": "ana", "startIndex": 3, "endIndex": 6},
                          {"signature": "na",  "startIndex": 4, "endIndex": 6}
                        ]}
                        """, JsonCompareMode.STRICT));
    }

    @Test
    void shouldReturnEmptyFindingsForEmptyContent() throws Exception {
        postScan("""
                {"content": "", "signatures": ["a"]}
                """)
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"findings": []}
                        """, JsonCompareMode.STRICT));
    }

    static Stream<String> invalidBodies() {
        String oversizedContent = "a".repeat(1024 * 1024 + 1);
        String tooManySignatures = IntStream.range(0, 101)
                .mapToObj(i -> "\"sig" + i + "\"")
                .collect(Collectors.joining(",", "[", "]"));
        return Stream.of(
                "{\"signatures\": [\"a\"]}",
                "{\"content\": null, \"signatures\": [\"a\"]}",
                "{\"content\": \"" + oversizedContent + "\", \"signatures\": [\"a\"]}",
                "{\"content\": \"abc\"}",
                "{\"content\": \"abc\", \"signatures\": null}",
                "{\"content\": \"abc\", \"signatures\": []}",
                "{\"content\": \"abc\", \"signatures\": " + tooManySignatures + "}",
                "{\"content\": \"abc\", \"signatures\": [\"a\", null]}",
                "{\"content\": \"abc\", \"signatures\": [\"\"]}",
                "{\"content\": \"abc\", \"signatures\": [\"" + "a".repeat(257) + "\"]}",
                "{\"content\": \"abc\", \"signatures\": [\"a\"",
                "not json",
                ""
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    void shouldReturnBadRequestProblemDetailForInvalidInput(String body) throws Exception {
        postScan(body)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }
}
