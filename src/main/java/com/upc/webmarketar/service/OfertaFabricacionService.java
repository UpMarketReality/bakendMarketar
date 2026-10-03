package com.upc.webmarketar.service;

import com.upc.webmarketar.dto.OfertaFabricacionRequestDTO;
import com.upc.webmarketar.dto.OfertaFabricacionResponseDTO;
import com.upc.webmarketar.entities.Cotizacionprototipo;
import com.upc.webmarketar.entities.Solicitud;
import com.upc.webmarketar.entities.Vendedor;
import com.upc.webmarketar.repositories.CotizacionPrototipoRepository;
import com.upc.webmarketar.repositories.SolicitudRepository;
import com.upc.webmarketar.repositories.VendedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class OfertaFabricacionService {

    private final CotizacionPrototipoRepository cotizacionPrototipoRepository;
    private final SolicitudRepository solicitudRepository;
    private final VendedorRepository vendedorRepository;

    public OfertaFabricacionService(
            CotizacionPrototipoRepository cotizacionPrototipoRepository,
            SolicitudRepository solicitudRepository,
            VendedorRepository vendedorRepository
    ) {
        this.cotizacionPrototipoRepository = cotizacionPrototipoRepository;
        this.solicitudRepository = solicitudRepository;
        this.vendedorRepository = vendedorRepository;
    }

    @Transactional
    public OfertaFabricacionResponseDTO presentarOferta(
            Long idSolicitud,
            String correoVendedor,
            OfertaFabricacionRequestDTO datos
    ) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() ->
                        new RuntimeException("Solicitud no encontrada")
                );

        if (!"EN_COTIZACION".equals(solicitud.getEstado())) {
            throw new RuntimeException(
                    "La solicitud no se encuentra abierta para cotización"
            );
        }

        Vendedor vendedor = vendedorRepository
                .findByUsuarioid_Correo(correoVendedor)
                .orElseThrow(() ->
                        new RuntimeException("Vendedor no encontrado")
                );

        boolean ofertaExistente =
                cotizacionPrototipoRepository
                        .existsByIdsolicitud_IdAndIdvendedor_Id(
                                idSolicitud,
                                vendedor.getId()
                        );

        if (ofertaExistente) {
            throw new RuntimeException(
                    "El vendedor ya presentó una oferta para esta solicitud"
            );
        }

        Cotizacionprototipo oferta = new Cotizacionprototipo();

        oferta.setEstado("PENDIENTE");
        oferta.setComentario(datos.getCondiciones());
        oferta.setMoneda(datos.getMoneda());
        oferta.setFecha(OffsetDateTime.now());
        oferta.setFechavencimiento(datos.getFechaVencimiento());
        oferta.setMonto(datos.getMonto());
        oferta.setTiempoentrega(datos.getDiasEntrega());
        oferta.setIdsolicitud(solicitud);
        oferta.setIdvendedor(vendedor);

        Cotizacionprototipo ofertaGuardada =
                cotizacionPrototipoRepository.save(oferta);

        return new OfertaFabricacionResponseDTO(
                ofertaGuardada.getId(),
                solicitud.getId(),
                vendedor.getId(),
                ofertaGuardada.getMonto(),
                ofertaGuardada.getMoneda(),
                ofertaGuardada.getTiempoentrega(),
                ofertaGuardada.getFechavencimiento(),
                ofertaGuardada.getComentario(),
                ofertaGuardada.getEstado()
        );
    }
}
