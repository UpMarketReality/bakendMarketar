package com.upc.webmarketar.infrastructure;

import com.upc.webmarketar.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/** Direct REST client. No retries: each generation can incur consumption. */
@Component
public class CloudflareImageClient {
    private final String account, token, base, model;
    private final int steps;
    private final JsonMapper json;
    private final HttpClient http;
    private final Duration timeout;

    @org.springframework.beans.factory.annotation.Autowired
    public CloudflareImageClient(JsonMapper json,
            @Value("${marketar.ai.account-id:}") String account,
            @Value("${marketar.ai.token:}") String token,
            @Value("${marketar.ai.base-url:https://api.cloudflare.com/client/v4}") String base,
            @Value("${marketar.ai.model:@cf/black-forest-labs/flux-1-schnell}") String model,
            @Value("${marketar.ai.steps:4}") int steps) {
        this(json, account, token, base, model, steps,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build(), Duration.ofMinutes(3));
    }

    CloudflareImageClient(JsonMapper json, String account, String token, String base,
            String model, int steps, HttpClient http, Duration timeout) {
        this.json = json; this.account = account; this.token = token; this.base = base;
        this.model = model; this.steps = steps; this.http = http; this.timeout = timeout;
    }

    public String model() { return model; }

    public void validate(String prompt) {
        if (prompt == null || prompt.isBlank() || prompt.codePointCount(0, prompt.length()) > 2048)
            throw ApiException.bad("El prompt final, incluidas instrucciones y especificaciones, debe tener entre 1 y 2048 caracteres. Reduce el texto sin omitir condiciones importantes.");
        if (account.isBlank() || token.isBlank())
            throw new ApiException(503, "La generación IA no está configurada");
        // Only the documented FLUX schema is supported; other models need their own adapter.
        if (!model.equals("@cf/black-forest-labs/flux-1-schnell") || steps < 1 || steps > 8
                || !account.matches("[a-zA-Z0-9_-]+"))
            throw new ApiException(503, "Configuración IA inválida: se requiere FLUX schnell y steps entre 1 y 8");
    }

    public byte[] generate(String prompt) {
        validate(prompt);
        HttpResponse<String> response;
        try {
            var request = HttpRequest.newBuilder(URI.create(base + "/accounts/" + account + "/ai/run/" + model))
                    .timeout(timeout).header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(Map.of("prompt", prompt, "steps", steps))))
                    .build();
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            throw new ApiException(504, "El proveedor IA excedió el tiempo de espera");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(503, "Generación interrumpida");
        } catch (IOException e) {
            throw new ApiException(503, "Proveedor IA temporalmente no disponible");
        } catch (IllegalArgumentException e) {
            throw new ApiException(503, "Configuración IA inválida");
        }
        int status = response.statusCode();
        if (status == 401 || status == 403 || status == 429 || status == 402 || status >= 500)
            throw new ApiException(503, "Proveedor IA no disponible: revisa credenciales, permisos, cuota o disponibilidad");
        if (status < 200 || status >= 300)
            throw new ApiException(502, "El proveedor IA rechazó la solicitud");
        try {
            var envelope = json.readTree(response.body());
            if (!envelope.path("success").isBoolean()) throw new IllegalArgumentException();
            if (!envelope.path("success").asBoolean())
                throw new ApiException(503, "El proveedor IA no pudo ejecutar la generación; revisa cuota y disponibilidad");
            var errors = envelope.path("errors");
            if (!errors.isArray() || !errors.isEmpty()) throw new IllegalArgumentException();
            var image = envelope.path("result").path("image");
            if (!image.isString() || image.asText().isBlank()) throw new IllegalArgumentException();
            byte[] bytes = Base64.getDecoder().decode(image.asText());
            if (bytes.length == 0 || bytes.length > 25 * 1024 * 1024) throw new IllegalArgumentException();
            // Decode pixels as well as inspect the signature: Base64 alone is not an image.
            if (ImageIO.read(new ByteArrayInputStream(bytes)) == null) throw new IllegalArgumentException();
            boolean png = bytes.length >= 8 && bytes[0] == (byte)137 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71;
            boolean jpeg = bytes.length >= 3 && bytes[0] == (byte)255 && bytes[1] == (byte)216 && bytes[2] == (byte)255;
            if (!png && !jpeg) throw new IllegalArgumentException();
            return bytes;
        } catch (ApiException e) { throw e;
        } catch (Exception e) {
            throw new ApiException(502, "Respuesta del proveedor IA inválida: no contiene una imagen JPEG o PNG válida");
        }
    }
}
