package com.upc.webmarketar.serviceimpl;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.entities.*;
import com.upc.webmarketar.exceptions.ApiException;
import com.upc.webmarketar.infrastructure.*;
import com.upc.webmarketar.repositories.*;
import com.upc.webmarketar.security.services.Identity;
import com.upc.webmarketar.services.PrototypeService;
import com.upc.webmarketar.services.ImageGenerationService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;

import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PrototypeServiceImpl implements PrototypeService {
    private final PrototipoIARepository prototypes;
    private final ImagenPrototipoRepository images;
    private final CategoriaPrototipoRepository categories;
    private final CompradorRepository buyers;
    private final Identity identity;
    private final ImageGenerationService provider;
    private final MediaStorage media;
    private final JsonMapper json;
    private final ExecutorService executor;
    private final TransactionTemplate tx;

    public PrototypeServiceImpl(
            PrototipoIARepository prototypes,
            ImagenPrototipoRepository images,
            CategoriaPrototipoRepository categories,
            CompradorRepository buyers,
            Identity identity,
            ImageGenerationService provider,
            MediaStorage media,
            JsonMapper json,
            @Qualifier("prototypeExecutor") ExecutorService executor,
            PlatformTransactionManager manager) {
        this.prototypes = prototypes;
        this.images = images;
        this.categories = categories;
        this.buyers = buyers;
        this.identity = identity;
        this.provider = provider;
        this.media = media;
        this.json = json;
        this.executor = executor;
        this.tx = new TransactionTemplate(manager);
        this.tx.setPropagationBehavior(
                org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    private PrototypeDTO view(Prototipoia p) {
        var imgs =
                images.findByIdprototipoia_IdOrderByEsprincipalDescIdAsc(p.getId()).stream()
                        .map(i -> new ImageDTO(i.getId(), i.getUrl(), i.getEsprincipal()))
                        .toList();
        return new PrototypeDTO(
                p.getId(),
                p.getNombreprototipo(),
                p.getIdcategoriaprototipo().getId(),
                p.getPrompt(),
                p.getEspecificaciones(),
                p.getEstadogeneracion(),
                p.getMensajeerrorseguro(),
                p.getFechacreacion(),
                imgs);
    }

    @Override
    @PreAuthorize("hasRole('COMPRADOR')")
    public PrototypeDTO own(long id) {
        var p = prototypes.findById(id).orElseThrow(ApiException::missing);
        if (p.getComprador().getId() != identity.buyer()) throw ApiException.missing();
        return view(p);
    }

    @Override
    @PreAuthorize("hasRole('COMPRADOR')")
    public PageDTO<PrototypeDTO> list(Long category, int page, int size) {
        return Pages.view(
                prototypes.search(
                        identity.buyer(),
                        category,
                        Pages.request(
                                page, size, Sort.by(Sort.Direction.DESC, "fechacreacion", "id"))),
                this::view);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('COMPRADOR')")
    public PrototypeDTO generate(PrototypeRequest r) {
        long buyer = identity.buyer();
        var category =
                categories
                        .findById(r.categoryId())
                        .orElseThrow(() -> ApiException.bad("Categoría inválida"));
        validateSpecifications(r.specifications(), category.getTipo());
        String specs = json.writeValueAsString(r.specifications());
        if (specs.length() > 2000) throw ApiException.bad("Especificaciones demasiado extensas");
        String prompt =
                "Referencia visual de producto personalizado. "
                        + r.prompt()
                        + ". Especificaciones: "
                        + specs;
        provider.validate(prompt);
        Prototipoia p = new Prototipoia();
        p.setNombreprototipo(r.name());
        p.setPrompt(r.prompt());
        p.setEspecificaciones(specs);
        p.setModeloia(provider.model());
        p.setEstadogeneracion("PENDIENTE");
        p.setComprador(buyers.getReferenceById(buyer));
        p.setIdcategoriaprototipo(category);
        p.setFechacreacion(Times.now());
        p.setFechaactualizacion(p.getFechacreacion());
        prototypes.save(p);
        var result = view(p);
        long id = p.getId();
        Runnable generation = RequestIdTask.wrap(() -> process(id, prompt));
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            executor.execute(generation);
                        } catch (RejectedExecutionException e) {
                            log.warn("operation=prototype_generation prototypeId={} state=ERROR reason=queue_full", id);
                            fail(id, "Servicio ocupado. Crea una nueva generación más tarde.");
                        }
                    }
                });
        return result;
    }

    private void validateSpecifications(Map<String, Object> s, String type) {
        for (String k : List.of("material", "color"))
            if (!(s.get(k) instanceof String v) || v.isBlank())
                throw ApiException.bad("Especificaciones requieren material y color");
        if ("MOBILIARIO".equals(type)) {
            if (!(s.get("dimensions") instanceof Map<?, ?> dims))
                throw ApiException.bad(
                        "Muebles requieren dimensions con width, height, depth y unit");
            for (String k : List.of("width", "height", "depth")) {
                try {
                    if (new BigDecimal(String.valueOf(dims.get(k))).signum() <= 0)
                        throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    throw ApiException.bad("Dimensiones positivas obligatorias");
                }
            }
            if (!Set.of("mm", "cm", "m").contains(String.valueOf(dims.get("unit"))))
                throw ApiException.bad("Unidad permitida: mm, cm o m");
        }
    }

    private void state(long id, String state, String error) {
        var p = prototypes.findById(id).orElseThrow(ApiException::missing);
        p.setEstadogeneracion(state);
        p.setMensajeerrorseguro(error);
        p.setFechaactualizacion(Times.now());
    }

    private void process(long id, String prompt) {
        long started = System.nanoTime();
        log.debug("operation=prototype_generation prototypeId={} model={}", id, provider.model());
        try {
            tx.executeWithoutResult(t -> state(id, "GENERANDO", null));
            byte[] data = provider.generate(prompt);
            tx.executeWithoutResult(
                    t -> {
                        String url = media.prototype(id, data);
                        Imagenprototipo image = new Imagenprototipo();
                        image.setIdprototipoia(prototypes.getReferenceById(id));
                        image.setUrl(url);
                        image.setEsprincipal(true);
                        images.save(image);
                        state(id, "COMPLETADO", null);
                    });
            log.info("operation=prototype_generation prototypeId={} state=COMPLETADO durationMs={}", id, (System.nanoTime() - started) / 1_000_000);
        } catch (Exception e) {
            if (e instanceof ApiException api)
                log.warn("operation=prototype_generation prototypeId={} state=ERROR status={} durationMs={}", id, api.status, (System.nanoTime() - started) / 1_000_000);
            else
                log.error("operation=prototype_generation prototypeId={} state=ERROR exceptionType={}", id, e.getClass().getName(), RequestLog.safeTrace(e));
            fail(id, e instanceof ApiException ? e.getMessage() : "No se pudo generar la imagen. Intenta una nueva generaci\u00f3n.");
        }
    }

    private void fail(long id, String message) {
        tx.executeWithoutResult(t -> state(id, "ERROR", message));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void authorizeImage(long prototypeId) {
        if (prototypes.countAuthorized(prototypeId, identity.user()) == 0)
            throw ApiException.missing();
    }
}