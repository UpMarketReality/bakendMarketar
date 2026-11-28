package com.upc.webmarketar.dtos;

import java.time.OffsetDateTime;
import java.util.List;

public record PrototypeDTO(
        long prototypeId,
        String name,
        long categoryId,
        String prompt,
        String specifications,
        String generationStatus,
        String error,
        OffsetDateTime createdAt,
        List<ImageDTO> images) {}
