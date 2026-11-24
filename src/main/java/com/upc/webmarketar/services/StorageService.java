package com.upc.webmarketar.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Almacenamiento de imágenes de productos.
 *
 * <p>Hoy se usa {@code LocalStorageService} (disco local, servido en /media/products/**).
 * Para migrar a AWS basta con crear otra implementación (por ejemplo {@code S3StorageService})
 * y activar {@code marketar.storage.type=s3}: el resto del código no cambia.
 */
public interface StorageService {

    /**
     * Guarda la imagen y devuelve la URL que se almacena en la base de datos.
     * En local es relativa (/media/products/x.png); en S3 sería absoluta (https://...).
     */
    String upload(MultipartFile file);

    /** Elimina la imagen solo si la transacción termina bien (si falla, no se borra nada). */
    void delete(String fileUrl);
}
