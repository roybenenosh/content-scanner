package com.scanner.api;

import com.scanner.core.ScanRequestValidator;
import com.scanner.core.SignatureScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScannerConfig {

    @Bean
    public SignatureScanner signatureScanner() {
        return new SignatureScanner();
    }

    @Bean
    public ScanRequestValidator scanRequestValidator() {
        return new ScanRequestValidator();
    }
}
