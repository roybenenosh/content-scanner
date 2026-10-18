package com.scanner.core;

import com.scanner.model.Finding;
import com.scanner.model.ScanRequest;
import com.scanner.model.ScanResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Finds every literal, case-sensitive occurrence (overlaps included) of each signature in the content.
 * Assumes the request was already validated by {@link ScanRequestValidator}.
 */
public class SignatureScanner implements Scanner<ScanRequest, ScanResult> {

    private static final Comparator<Finding> BY_START_THEN_SIGNATURE =
            Comparator.comparingInt(Finding::startIndex).thenComparing(Finding::signature);

    @Override
    public ScanResult scan(ScanRequest request) {
        String content = request.content();
        List<Finding> findings = new ArrayList<>();

        for (String signature : new LinkedHashSet<>(request.signatures())) {
            // Advance by one (not by signature length) so overlapping matches are reported.
            for (int index = content.indexOf(signature); index >= 0; index = content.indexOf(signature, index + 1)) {
                findings.add(new Finding(signature, index, index + signature.length()));
            }
        }

        findings.sort(BY_START_THEN_SIGNATURE);
        return new ScanResult(List.copyOf(findings));
    }
}
