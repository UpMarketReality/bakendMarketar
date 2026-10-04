package com.upc.webmarketar.controllers;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.infrastructure.MediaStorage;
import com.upc.webmarketar.security.dtos.*;
import com.upc.webmarketar.services.PrototypeService;

import jakarta.validation.Valid;

import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prototypes")
public class PrototypeController {
    private final PrototypeService service;
    private final MediaStorage media;

    public PrototypeController(PrototypeService s, MediaStorage m) {
        service = s;
        media = m;
    }

    @PostMapping
    public ResponseEntity<PrototypeDTO> generate(@Valid @RequestBody PrototypeRequest r) {
        return ResponseEntity.accepted().body(service.generate(r));
    }

    @GetMapping("/me")
    public PageDTO<PrototypeDTO> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(categoryId, page, size);
    }

    @GetMapping("/{prototypeId}")
    public PrototypeDTO own(@PathVariable long prototypeId) {
        return service.own(prototypeId);
    }

    @GetMapping("/{prototypeId}/images/{filename}")
    public ResponseEntity<Resource> image(
            @PathVariable long prototypeId, @PathVariable String filename) {
        service.authorizeImage(prototypeId);
        var path = media.privateFile(prototypeId, filename);
        String type =
                filename.endsWith(".png")
                        ? "image/png"
                        : filename.endsWith(".jpg") ? "image/jpeg" : "image/webp";
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType(type))
                .body(new FileSystemResource(path));
    }
}