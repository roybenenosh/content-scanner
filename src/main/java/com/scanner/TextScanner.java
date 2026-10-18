package com.scanner;

import com.scanner.core.Scanner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextScanner implements Scanner<byte[], List<String>> {
    @Override
    public List<String> scan(byte[] file) {
        String text = shortenText(file);
        float[] embeddings = runTextScan(text);
        return matchEmbeddings(embeddings);
    }

    private String shortenText(byte[] file) {
        // Shorten text, should run on cpu
        String fileContent = new String(file);
        return fileContent.substring(0, 200);
    }

    private float[] runTextScan(String text) {
        // Heavy text ML processing (takes 5 seconds on cpu, 100ms on gpu)
        return new float[] {1,2,3};
    }

    private List<String> matchEmbeddings(float[] embeddings) {
        // Easy matching logic - runs fast on cpu
        return List.of("resume");
    }
}
