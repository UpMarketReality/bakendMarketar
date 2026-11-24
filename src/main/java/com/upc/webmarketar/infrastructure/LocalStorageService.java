package com.upc.webmarketar.infrastructure;

import com.upc.webmarketar.services.StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Guarda las imágenes en el disco local, en la carpeta definida por {@code marketar.storage}
 * (por defecto ./media). Se sirven públicamente en http://localhost:8080/media/products/...
 * (ver WebConfig y SecurityConfig).
 *
 * <p>Se activa con {@code marketar.storage.type=local} (valor por defecto).
 */
@Service
@ConditionalOnProperty(name = "marketar.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final MediaStorage media;

    public LocalStorageService(MediaStorage media) {
        this.media = media;
    }

    @Override
    public String upload(MultipartFile file) {
        // Valida tamaño (máx. 5 MB) y firma real del archivo (JPEG, PNG o WebP)
        return media.product(file);
    }

    @Override
    public void delete(String fileUrl) {
        media.deleteProductAfterCommit(fileUrl);
    }
}
