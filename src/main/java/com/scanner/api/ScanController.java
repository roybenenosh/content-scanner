package com.scanner.api;

import com.scanner.core.InvalidScanRequestException;
import com.scanner.core.ScanRequestValidator;
import com.scanner.core.SignatureScanner;
import com.scanner.model.ScanRequest;
import com.scanner.model.ScanResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ScanController {

    private final SignatureScanner scanner;
    private final ScanRequestValidator validator;

    public ScanController(SignatureScanner scanner, ScanRequestValidator validator) {
        this.scanner = scanner;
        this.validator = validator;
    }

    @PostMapping(path = "/scan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ScanResult scan(@RequestBody ScanRequest request) {
        try {
            validator.validate(request);
        } catch (InvalidScanRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return scanner.scan(request);
    }
}
