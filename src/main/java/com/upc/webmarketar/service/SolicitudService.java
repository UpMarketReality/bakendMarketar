package com.upc.webmarketar.service;

import com.upc.webmarketar.dto.SolicitudResumenDTO;
import com.upc.webmarketar.entities.Solicitud;
import com.upc.webmarketar.repositories.SolicitudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudService {
    private final SolicitudRepository solicitudRepository;

    public SolicitudService(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @Transactional(readOnly = true)
    public Page<SolicitudResumenDTO> listarSolicitudesAbiertas(
            Long categoryId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return solicitudRepository
                .buscarSolicitudesAbiertas(categoryId, pageable)
                .map(this::convertirAResumen);
    }

    private SolicitudResumenDTO convertirAResumen(Solicitud solicitud) {

        return new SolicitudResumenDTO(
                solicitud.getId(),

                solicitud.getIdprototipoia().getId(),
                solicitud.getIdprototipoia().getNombreprototipo(),

                solicitud.getIdprototipoia()
                        .getIdcategoriaprototipo()
                        .getId(),

                solicitud.getIdprototipoia()
                        .getIdcategoriaprototipo()
                        .getNombre(),

                solicitud.getCantidad(),
                solicitud.getZonaentrega(),

                solicitud.getFechasolicitud().toInstant(),
                solicitud.getEstado()
        );
    }

}
