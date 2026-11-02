package com.upc.webmarketar.controllers;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.services.CatalogService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CatalogController {
    private final CatalogService service;

    public CatalogController(CatalogService s) {
        service = s;
    }

    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    @GetMapping("/products")
    public PageDTO<ProductDTO> products(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(search, categoryId, minPrice, maxPrice, page, size, false);
    }

    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    @GetMapping("/products/{productId}")
    public ProductDTO product(@PathVariable long productId) {
        return service.publicProduct(productId);
    }

    @GetMapping("/seller/products")
    public PageDTO<ProductDTO> own(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(null, null, null, null, page, size, true);
    }

    @GetMapping("/seller/products/{productId}")
    public ProductDTO own(@PathVariable long productId) {
        return service.own(productId);
    }

    @PostMapping(
            value = "/seller/products",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @Encoding(
                                    name = "data",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    }
            )
    )
    public ResponseEntity<ProductDTO> create(
            @Valid @RequestPart("data") ProductRequest data,
            @RequestPart("images") List<MultipartFile> images) {

        return ResponseEntity.status(201).body(service.create(data, images));
    }

    @PutMapping(
            value = "/seller/products/{productId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @Encoding(
                                    name = "data",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    }
            )
    )
    public ProductDTO update(
            @PathVariable long productId,
            @Valid @RequestPart("data") ProductRequest data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        return service.update(productId, data, images);
    }

    @PatchMapping("/seller/products/{productId}/status")
    public ProductDTO status(@PathVariable long productId, @Valid @RequestBody ActiveRequest data) {
        return service.status(productId, data.active());
    }

    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    @GetMapping("/product-categories")
    public List<CategoryDTO> categories() {
        return service.categories(false);
    }

    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    @GetMapping("/prototype-categories")
    public List<CategoryDTO> prototypeCategories() {
        return service.categories(true);
    }

    @GetMapping("/seller/store")
    public StoreDTO store() {
        return service.store();
    }

    @PutMapping("/seller/store")
    public StoreDTO store(@Valid @RequestBody StoreRequest data) {
        return service.store(data);
    }
}
