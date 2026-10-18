package com.scanner.core;

import com.scanner.model.ScanRequest;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

public class ScanRequestValidator {

    static final int MAX_CONTENT_BYTES = 1024 * 1024;
    static final int MAX_DISTINCT_SIGNATURES = 100;
    // Measured in String.length() (UTF-16 code units), the same unit as finding indices.
    static final int MAX_SIGNATURE_LENGTH = 256;

    public void validate(ScanRequest request) {
        if (request == null) {
            throw new InvalidScanRequestException("request body is required");
        }
        validateContent(request.content());
        validateSignatures(request.signatures());
    }

    private void validateContent(String content) {
        if (content == null) {
            throw new InvalidScanRequestException("content is required");
        }
        if (content.getBytes(StandardCharsets.UTF_8).length > MAX_CONTENT_BYTES) {
            throw new InvalidScanRequestException("content must not exceed 1 MiB (UTF-8 encoded)");
        }
    }

    private void validateSignatures(List<String> signatures) {
        if (signatures == null || signatures.isEmpty()) {
            throw new InvalidScanRequestException("signatures must contain at least one entry");
        }
        for (String signature : signatures) {
            if (signature == null || signature.isEmpty()) {
                throw new InvalidScanRequestException("signatures must not contain null or empty entries");
            }
            if (signature.length() > MAX_SIGNATURE_LENGTH) {
                throw new InvalidScanRequestException(
                        "each signature must be at most " + MAX_SIGNATURE_LENGTH + " characters");
            }
        }
        // Duplicates are treated as one signature, so the limit applies to distinct entries.
        if (new HashSet<>(signatures).size() > MAX_DISTINCT_SIGNATURES) {
            throw new InvalidScanRequestException(
                    "signatures must not exceed " + MAX_DISTINCT_SIGNATURES + " distinct entries");
        }
    }
}