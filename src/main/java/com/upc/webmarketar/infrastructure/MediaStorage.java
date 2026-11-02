package com.upc.webmarketar.infrastructure;

import com.upc.webmarketar.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class MediaStorage {
    private final Path root;

    public MediaStorage(@Value("${marketar.storage}") String path) {
        root = Path.of(path).toAbsolutePath().normalize();
    }

    public Path root() {
        return root;
    }

    public String product(MultipartFile f) {
        try {
            if (f.isEmpty() || f.getSize() > 5L * 1024 * 1024)
                throw ApiException.bad("Cada imagen debe pesar entre 1 byte y 5 MB");
            byte[] bytes = f.getBytes();
            String ext = extension(bytes);
            String name = UUID.randomUUID() + ext;
            Path p = root.resolve("products").resolve(name);
            Files.createDirectories(p.getParent());
            Files.write(p, bytes);
            rollbackDelete(p);
            return "/media/products/" + name;
        } catch (IOException e) {
            throw new ApiException(503, "No se pudo guardar la imagen");
        }
    }

    public String prototype(long id, byte[] bytes) {
        if (bytes.length == 0 || bytes.length > 25 * 1024 * 1024)
            throw new ApiException(502, "Imagen del proveedor no válida");
        try {
            String name = UUID.randomUUID() + extension(bytes);
            Path p = root.resolve("prototypes").resolve(Long.toString(id)).resolve(name);
            Files.createDirectories(p.getParent());
            rollbackDelete(p);
            try {
                Files.write(p, bytes);
            } catch (IOException e) {
                Files.deleteIfExists(p);
                throw e;
            }
            return "/api/v1/prototypes/" + id + "/images/" + name;
        } catch (IOException e) {
            throw new ApiException(503, "No se pudo guardar la imagen");
        }
    }

    private String extension(byte[] b) {
        if (b.length >= 8
                && b[0] == (byte) 137
                && b[1] == 80
                && b[2] == 78
                && b[3] == 71
                && b[4] == 13
                && b[5] == 10
                && b[6] == 26
                && b[7] == 10) return ".png";
        if (b.length >= 3 && b[0] == (byte) 255 && b[1] == (byte) 216 && b[2] == (byte) 255)
            return ".jpg";
        if (b.length >= 12
                && new String(b, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF")
                && new String(b, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP"))
            return ".webp";
        throw ApiException.bad("Solo se permiten archivos JPEG, PNG o WebP con firma válida");
    }

    private void rollbackDelete(Path p) {
        if (TransactionSynchronizationManager.isSynchronizationActive())
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status != STATUS_COMMITTED)
                                try {
                                    Files.deleteIfExists(p);
                                } catch (IOException ignored) {
                                }
                        }
                    });
    }

    public void deleteProductAfterCommit(String url) {
        if (!url.startsWith("/media/products/")) return;
        Path p =
                root.resolve("products")
                        .resolve(url.substring("/media/products/".length()))
                        .normalize();
        if (!p.startsWith(root.resolve("products"))) return;
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    }
                });
    }

    public Path privateFile(long id, String name) {
        if (!name.matches("[a-f0-9-]+\\.(png|jpg|webp)")) throw ApiException.missing();
        Path p = root.resolve("prototypes").resolve(Long.toString(id)).resolve(name);
        if (!Files.isRegularFile(p)) throw ApiException.missing();
        return p;
    }
}
