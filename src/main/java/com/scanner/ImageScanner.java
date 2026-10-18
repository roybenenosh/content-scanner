package com.scanner;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImageScanner {
    public List<String> scanImage(byte[] file) {
        byte[] image = shrinkImage(file);
        float[] embeddings = runImageScan(image);
        return matchEmbeddings(embeddings);
    }

    private byte[] shrinkImage(byte[] file) {

        // Shrink image, should run on cpu
        return file;
    }

    private float[] runImageScan(byte[] image) {
        // Heavy image ML processing (takes 5 seconds on cpu, 100ms on gpu)
        return new float[] {1,2,3};
    }

    private List<String> matchEmbeddings(float[] embeddings) {
        // Easy matching logic - runs fast on cpu
        return List.of("passport");
    }
}
