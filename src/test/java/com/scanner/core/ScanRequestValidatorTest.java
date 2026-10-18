package com.scanner.core;

import com.scanner.model.ScanRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScanRequestValidatorTest {

    private static final int MIB = 1024 * 1024;

    private final ScanRequestValidator validator = new ScanRequestValidator();

    private void assertValid(String content, List<String> signatures) {
        assertDoesNotThrow(() -> validator.validate(new ScanRequest(content, signatures)));
    }

    private void assertInvalid(String content, List<String> signatures) {
        assertThrows(InvalidScanRequestException.class, () -> validator.validate(new ScanRequest(content, signatures)));
    }

    private static List<String> distinctSignatures(int count) {
        return IntStream.range(0, count).mapToObj(i -> "sig" + i).toList();
    }

    @Test
    void shouldRejectNullRequest() {
        assertThrows(InvalidScanRequestException.class, () -> validator.validate(null));
    }

    @Test
    void shouldAcceptEmptyContent() {
        assertValid("", List.of("a"));
    }

    @Test
    void shouldRejectNullContent() {
        assertInvalid(null, List.of("a"));
    }

    @Test
    void shouldAcceptContentOfExactlyOneMib() {
        assertValid("a".repeat(MIB), List.of("a"));
    }

    @Test
    void shouldRejectContentOverOneMib() {
        assertInvalid("a".repeat(MIB + 1), List.of("a"));
    }

    @Test
    void shouldMeasureContentSizeInUtf8Bytes() {
        // "é" is 1 char but 2 UTF-8 bytes: well under 1M chars, just over 1 MiB.
        assertInvalid("é".repeat(MIB / 2 + 1), List.of("a"));
    }

    @Test
    void shouldRejectNullSignatures() {
        assertInvalid("abc", null);
    }

    @Test
    void shouldRejectEmptySignatures() {
        assertInvalid("abc", List.of());
    }

    @Test
    void shouldRejectNullSignatureEntry() {
        assertInvalid("abc", Arrays.asList("a", null));
    }

    @Test
    void shouldRejectEmptySignatureEntry() {
        assertInvalid("abc", List.of("a", ""));
    }

    @Test
    void shouldAcceptSignatureOf256Characters() {
        assertValid("abc", List.of("a".repeat(256)));
    }

    @Test
    void shouldRejectSignatureOver256Characters() {
        assertInvalid("abc", List.of("a".repeat(257)));
    }

    @Test
    void shouldAcceptExactly100DistinctSignatures() {
        assertValid("abc", distinctSignatures(100));
    }

    @Test
    void shouldRejectOver100DistinctSignatures() {
        assertInvalid("abc", distinctSignatures(101));
    }

    @Test
    void shouldCountSignatureLimitAfterDeduplication() {
        List<String> signatures = new ArrayList<>(distinctSignatures(100));
        signatures.addAll(distinctSignatures(50));

        assertValid("abc", signatures);
    }
}