package com.scanner.core;

public interface Scanner<I, O> {
    O scan(I input);
}
