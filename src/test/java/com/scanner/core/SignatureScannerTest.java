package com.scanner.core;

import com.scanner.model.Finding;
import com.scanner.model.ScanRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureScannerTest {

    private final SignatureScanner scanner = new SignatureScanner();

    private List<Finding> scan(String content, String... signatures) {
        return scanner.scan(new ScanRequest(content, List.of(signatures))).findings();
    }

    @Test
    void shouldReturnSpecExampleFindingsForBanana() {
        assertEquals(List.of(
                new Finding("ana", 1, 4),
                new Finding("na", 2, 4),
                new Finding("ana", 3, 6),
                new Finding("na", 4, 6)
        ), scan("banana", "ana", "na"));
    }

    @Test
    void shouldReturnOverlappingOccurrences() {
        assertEquals(List.of(
                new Finding("aa", 0, 2),
                new Finding("aa", 1, 3),
                new Finding("aa", 2, 4)
        ), scan("aaaa", "aa"));
    }

    @Test
    void shouldMatchCaseSensitively() {
        assertEquals(List.of(new Finding("Ab", 2, 4)), scan("abAb", "Ab"));
    }

    @Test
    void shouldTreatDuplicateSignaturesAsOne() {
        assertEquals(List.of(new Finding("na", 2, 4), new Finding("na", 4, 6)), scan("banana", "na", "na"));
    }

    @Test
    void shouldReturnEmptyListForEmptyContent() {
        assertTrue(scan("", "a").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNothingMatches() {
        assertTrue(scan("banana", "xyz").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenSignatureLongerThanContent() {
        assertTrue(scan("ab", "abc").isEmpty());
    }

    @Test
    void shouldSortBySignatureWhenStartIndexTies() {
        assertEquals(List.of(new Finding("a", 0, 1), new Finding("ab", 0, 2)), scan("ab", "ab", "a"));
    }

    @Test
    void shouldUseUtf16StringIndices() {
        // "😀" is a surrogate pair: two Java chars.
        assertEquals(List.of(new Finding("x", 2, 3)), scan("😀x", "x"));
    }
}