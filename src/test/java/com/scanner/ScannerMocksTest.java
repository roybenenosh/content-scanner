package com.scanner;

import com.scanner.core.Scanner;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScannerMocksTest {

    @Test
    void textScannerShouldReturnMockLabelThroughScannerInterface() {
        Scanner<byte[], List<String>> scanner = new TextScanner();
        byte[] text = "x".repeat(200).getBytes(StandardCharsets.UTF_8);

        assertEquals(List.of("resume"), scanner.scan(text));
    }

    @Test
    void imageScannerShouldReturnMockLabelThroughScannerInterface() {
        Scanner<byte[], List<String>> scanner = new ImageScanner();

        assertEquals(List.of("passport"), scanner.scan(new byte[] {1, 2, 3}));
    }
}