package com.scanner.model;

import java.util.List;

public record ScanResult(List<Finding> findings) {
}
