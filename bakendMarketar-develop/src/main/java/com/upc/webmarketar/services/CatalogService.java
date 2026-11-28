package com.upc.webmarketar.services;

import com.upc.webmarketar.dtos.*;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface CatalogService {
    ProductDTO product(long id);

    ProductDTO publicProduct(long id);

    ProductDTO own(long id);

    PageDTO<ProductDTO> list(
            String search,
            Long category,
            BigDecimal min,
            BigDecimal max,
            int page,
            int size,
            boolean own);

    ProductDTO create(ProductRequest p, List<MultipartFile> images);

    ProductDTO update(long id, ProductRequest p, List<MultipartFile> images);

    ProductDTO status(long id, boolean active);

    List<CategoryDTO> categories(boolean prototypes);

    StoreDTO store();

    StoreDTO store(StoreRequest p);
}
