package com.upc.webmarketar.serviceimpl;

import com.upc.webmarketar.infrastructure.CloudflareImageClient;
import com.upc.webmarketar.services.ImageGenerationService;
import org.springframework.stereotype.Service;

@Service
public class CloudflareImageServiceImpl implements ImageGenerationService {
    private final CloudflareImageClient client;
    public CloudflareImageServiceImpl(CloudflareImageClient client) { this.client = client; }
    public String model() { return client.model(); }
    public void validate(String prompt) { client.validate(prompt); }
    public byte[] generate(String prompt) { return client.generate(prompt); }
}
