package com.upc.webmarketar.serviceimpl;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.entities.*;
import com.upc.webmarketar.exceptions.ApiException;
import com.upc.webmarketar.repositories.*;
import com.upc.webmarketar.security.services.Identity;
import com.upc.webmarketar.services.QuoteService;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class QuoteServiceImpl implements QuoteService {
    private final SolicitudRepository requests;
    private final CotizacionPrototipoRepository offers;
    private final EncargoRepository orders;
    private final PrototipoIARepository prototypes;
    private final ImagenPrototipoRepository images;
    private final VendedorRepository sellers;
    private final Identity identity;

    public QuoteServiceImpl(
            SolicitudRepository requests,
            CotizacionPrototipoRepository offers,
            EncargoRepository orders,
            PrototipoIARepository prototypes,
            ImagenPrototipoRepository images,
            VendedorRepository sellers,
            Identity identity) {
        this.requests = requests;
        this.offers = offers;
        this.orders = orders;
        this.prototypes = prototypes;
        this.images = images;
        this.sellers = sellers;
        this.identity = identity;
    }

    private boolean open(String state) {
        return Set.of("EN_COTIZACION", "COTIZADA").contains(state);
    }

    private QuoteRequestDTO requestView(Solicitud s, boolean buyer) {
        var p = s.getIdprototipoia();
        return new QuoteRequestDTO(
                s.getId(),
                p.getId(),
                p.getNombreprototipo(),
                p.getIdcategoriaprototipo().getId(),
                s.getCantidad(),
                buyer ? s.getDireccionentrega() : null,
                s.getZonaentrega(),
                s.getCondicionesentrega(),
                s.getEspecificacionesacordadas(),
                List.copyOf(s.getImagenesreferencia()),
                s.getEstado(),
                s.getFechasolicitud(),
                offers.countByIdsolicitud_Id(s.getId()));
    }

    private OfferDTO offerView(Cotizacionprototipo o) {
        var v = o.getIdvendedor();
        return new OfferDTO(
                o.getId(),
                o.getIdsolicitud().getId(),
                v.getId(),
                v.getNombretienda(),
                o.getMonto(),
                o.getMoneda(),
                o.getTiempoentrega(),
                o.getFechavencimiento(),
                o.getComentario(),
                o.getEstado(),
                !o.getFechavencimiento().isAfter(Times.now()));
    }

    private OrderDTO orderView(Encargo e) {
        var s = e.getSolicitud();
        var o = e.getCotizacionprototipo();
        var v = o.getIdvendedor();
        return new OrderDTO(
                e.getId(),
                s.getId(),
                o.getId(),
                v.getId(),
                v.getNombretienda(),
                s.getIdprototipoia().getId(),
                e.getEstado(),
                o.getMonto(),
                o.getMoneda(),
                o.getTiempoentrega(),
                s.getCantidad(),
                s.getDireccionentrega(),
                o.getComentario(),
                s.getEspecificacionesacordadas(),
                List.copyOf(s.getImagenesreferencia()),
                e.getFechacreacion(),
                e.getFechaactualizacion());
    }

    private Solicitud ownRequestEntity(long id, boolean lock) {
        Solicitud s =
                (lock ? requests.lockById(id) : requests.findById(id))
                        .orElseThrow(ApiException::missing);
        if (s.getIdcomprador().getId() != identity.buyer()) throw ApiException.missing();
        return s;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('COMPRADOR')")
    public QuoteRequestDTO create(CreateQuoteRequest r) {
        long buyer = identity.buyer();
        var p = prototypes.lockById(r.prototypeId()).orElseThrow(ApiException::missing);
        if (p.getComprador().getId() != buyer) throw ApiException.missing();
        if (!"COMPLETADO".equals(p.getEstadogeneracion()))
            throw ApiException.conflict("El prototipo debe estar COMPLETADO");
        var urls =
                images.findByIdprototipoia_IdOrderByEsprincipalDescIdAsc(p.getId()).stream()
                        .sorted(Comparator.comparing(Imagenprototipo::getId))
                        .map(Imagenprototipo::getUrl)
                        .toList();
        if (urls.isEmpty()) throw ApiException.conflict("El prototipo no tiene imágenes");
        Solicitud s = new Solicitud();
        s.setIdcomprador(p.getComprador());
        s.setIdprototipoia(p);
        s.setEstado("EN_COTIZACION");
        s.setCantidad(r.quantity());
        s.setDireccionentrega(r.deliveryAddress());
        s.setZonaentrega(r.deliveryZone());
        s.setCondicionesentrega(r.deliveryConditions());
        s.setEspecificacionesacordadas(p.getEspecificaciones());
        s.setImagenesreferencia(urls);
        s.setFechasolicitud(Times.now());
        s.setFechaactualizacion(s.getFechasolicitud());
        requests.save(s);
        return requestView(s, true);
    }

    @Override
    @PreAuthorize("hasRole('COMPRADOR')")
    public QuoteRequestDTO ownRequest(long id) {
        return requestView(ownRequestEntity(id, false), true);
    }

    @Override
    @PreAuthorize("hasRole('VENDEDOR')")
    public QuoteRequestDTO sellerRequest(long id) {
        var s = requests.findById(id).orElseThrow(ApiException::missing);
        if (!open(s.getEstado())) throw ApiException.missing();
        return requestView(s, false);
    }

    @Override
    @PreAuthorize("(#buyer and hasRole('COMPRADOR')) or (!#buyer and hasRole('VENDEDOR'))")
    public PageDTO<QuoteRequestDTO> requests(boolean buyer, Long category, int page, int size) {
        return Pages.view(
                requests.search(
                        buyer ? identity.buyer() : null,
                        category,
                        Pages.request(
                                page, size, Sort.by(Sort.Direction.DESC, "fechasolicitud", "id"))),
                s -> requestView(s, buyer));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public OfferDTO offer(long request, OfferRequest r) {
        long seller = identity.seller();
        var s = requests.lockById(request).orElseThrow(ApiException::missing);
        if (!open(s.getEstado())) throw ApiException.conflict("Solicitud cerrada");
        if (!r.expiresAt().isAfter(Times.now()))
            throw ApiException.bad("El vencimiento debe ser futuro");
        if (offers.existsByIdsolicitud_IdAndIdvendedor_Id(request, seller))
            throw ApiException.conflict("Ya presentaste una oferta");
        Cotizacionprototipo o = new Cotizacionprototipo();
        o.setIdsolicitud(s);
        o.setIdvendedor(sellers.getReferenceById(seller));
        o.setEstado("PENDIENTE");
        o.setComentario(r.conditions());
        o.setMoneda(r.currency());
        o.setFechavencimiento(Times.database(r.expiresAt()));
        o.setMonto(r.amount().setScale(2));
        o.setTiempoentrega(r.deliveryDays());
        o.setFecha(Times.now());
        offers.save(o);
        s.setEstado("COTIZADA");
        s.setFechaactualizacion(Times.now());
        return offerView(o);
    }

    @Override
    @PreAuthorize("hasRole('COMPRADOR')")
    public List<OfferDTO> offers(long request) {
        ownRequestEntity(request, false);
        return offers.findByIdsolicitud_IdOrderByMontoAscIdAsc(request).stream()
                .map(this::offerView)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('COMPRADOR')")
    public ResultDTO<OrderDTO> accept(long offer) {
        var o = offers.findById(offer).orElseThrow(ApiException::missing);
        // Only the request ID is read before its lock; do not initialize the lazy request first.
        var s = ownRequestEntity(o.getIdsolicitud().getId(), true);
        var existing = orders.findBySolicitud_Id(s.getId());
        if (existing.isPresent()) {
            var e = existing.get();
            if (e.getCotizacionprototipo().getId() == offer)
                return new ResultDTO<>(false, orderView(e));
            throw ApiException.conflict("Otra oferta ya fue aceptada");
        }
        if (!open(s.getEstado())
                || !"PENDIENTE".equals(o.getEstado())
                || !o.getFechavencimiento().isAfter(Times.now())) {
            throw ApiException.conflict("Oferta vencida o solicitud cerrada");
        }
        for (var other : offers.findByIdsolicitud_IdAndEstado(s.getId(), "PENDIENTE")) {
            other.setEstado(other.getId() == offer ? "ACEPTADA" : "RECHAZADA");
        }
        s.setEstado("ACEPTADA");
        s.setFechaactualizacion(Times.now());
        Encargo e = new Encargo();
        e.setSolicitud(s);
        e.setCotizacionprototipo(o);
        e.setEstado("CONFIRMADO");
        e.setFechacreacion(Times.now());
        e.setFechaactualizacion(e.getFechacreacion());
        // Persist the winning state before creating its order; all changes share this transaction.
        offers.flush();
        orders.save(e);
        return new ResultDTO<>(true, orderView(e));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('COMPRADOR')")
    public QuoteRequestDTO cancel(long id) {
        var s = ownRequestEntity(id, true);
        if ("CANCELADA".equals(s.getEstado())) return requestView(s, true);
        if (!open(s.getEstado()))
            throw ApiException.conflict("Solo se cancelan solicitudes abiertas");
        for (var o : offers.findByIdsolicitud_IdAndEstado(id, "PENDIENTE"))
            o.setEstado("RECHAZADA");
        s.setEstado("CANCELADA");
        s.setFechaactualizacion(Times.now());
        return requestView(s, true);
    }

    private void authorizeOrder(Encargo e, boolean seller) {
        long owner =
                seller
                        ? e.getCotizacionprototipo().getIdvendedor().getId()
                        : e.getSolicitud().getIdcomprador().getId();
        if (owner != (seller ? identity.seller() : identity.buyer())) throw ApiException.missing();
    }

    @Override
    @PreAuthorize("(#seller and hasRole('VENDEDOR')) or (!#seller and hasRole('COMPRADOR'))")
    public OrderDTO order(long id, boolean seller) {
        var e = orders.findById(id).orElseThrow(ApiException::missing);
        authorizeOrder(e, seller);
        return orderView(e);
    }

    @Override
    @PreAuthorize("(#seller and hasRole('VENDEDOR')) or (!#seller and hasRole('COMPRADOR'))")
    public PageDTO<OrderDTO> orders(boolean seller, int page, int size) {
        return Pages.view(
                orders.search(
                        seller,
                        seller ? identity.seller() : identity.buyer(),
                        Pages.request(
                                page, size, Sort.by(Sort.Direction.DESC, "fechacreacion", "id"))),
                this::orderView);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public OrderDTO status(long id, String next) {
        var states = List.of("CONFIRMADO", "EN_FABRICACION", "LISTO", "ENVIADO", "ENTREGADO");
        if (!states.contains(next)) throw ApiException.bad("Estado desconocido");
        // Request before order: consistent with acceptance/cancellation.
        long requestId = orders.requestId(id).orElseThrow(ApiException::missing);
        requests.lockById(requestId).orElseThrow(ApiException::missing);
        var e = orders.lockById(id).orElseThrow(ApiException::missing);
        authorizeOrder(e, true);
        String current = e.getEstado();
        if (current.equals(next)) return orderView(e);
        if (states.indexOf(next) != states.indexOf(current) + 1)
            throw ApiException.conflict("No se permite saltar o retroceder etapas");
        e.setEstado(next);
        e.setFechaactualizacion(Times.now());
        if ("ENTREGADO".equals(next)) {
            e.getSolicitud().setEstado("COMPLETADA");
            e.getSolicitud().setFechaactualizacion(Times.now());
        }
        return orderView(e);
    }
}

