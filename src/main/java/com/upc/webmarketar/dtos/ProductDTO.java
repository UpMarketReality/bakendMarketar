package com.upc.webmarketar.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(
        long productId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal wholesalePrice,
        int stock,
        String status,
        String dimensions,
        String material,
        String color,
        long categoryId,
        long sellerId,
        String storeName,
        String mainImageUrl,
        List<ImageDTO> images) {}
