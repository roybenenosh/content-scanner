package com.scanner.model;

import java.util.List;

public record ScanRequest(String content, List<String> signatures) {
}
