package com.upc.webmarketar.services;

public interface ImageGenerationService {
    String model();
    void validate(String prompt);
    byte[] generate(String prompt);
}
