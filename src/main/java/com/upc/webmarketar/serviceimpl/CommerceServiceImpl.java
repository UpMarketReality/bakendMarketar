package com.upc.webmarketar.serviceimpl;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.entities.*;
import com.upc.webmarketar.exceptions.ApiException;
import com.upc.webmarketar.repositories.*;
import com.upc.webmarketar.security.services.Identity;
import com.upc.webmarketar.services.*;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasRole('COMPRADOR')")
public class CommerceServiceImpl implements CommerceService {
    private final CompradorRepository buyers;
    private final ProductoRepository products;
    private final FavoritoRepository favorites;
    private final CarritoRepository carts;
    private final CarritoDetalleRepository details;
    private final BoletaVentaRepository purchases;
    private final BoletaDetalleRepository purchaseDetails;
    private final Identity identity;
    private final CatalogService catalog;

    public CommerceServiceImpl(
            CompradorRepository buyers,
            ProductoRepository products,
            FavoritoRepository favorites,
            CarritoRepository carts,
            CarritoDetalleRepository details,
            BoletaVentaRepository purchases,
            BoletaDetalleRepository purchaseDetails,
            Identity identity,
            CatalogService catalog) {
        this.buyers = buyers;
        this.products = products;
        this.favorites = favorites;
        this.carts = carts;
        this.details = details;
        this.purchases = purchases;
        this.purchaseDetails = purchaseDetails;
        this.identity = identity;
        this.catalog = catalog;
    }

    private Comprador lockBuyer() {
        return buyers.lockById(identity.buyer()).orElseThrow(ApiException::missing);
    }

    private Carrito active(Comprador buyer) {
        return carts.findByIdcomprador_IdAndEstado(buyer.getId(), "ACTIVO")
                .orElseGet(
                        () -> {
                            Carrito cart = new Carrito();
                            cart.setIdcomprador(buyer);
                            cart.setEstado("ACTIVO");
                            cart.setFechacreacion(Times.now());
                            return carts.save(cart);
                        });
    }

    private FavoriteDTO favoriteView(Favorito f) {
        return new FavoriteDTO(
                f.getId(), f.getIdproducto().getId(), catalog.product(f.getIdproducto().getId()));
    }

    @Override
    @Transactional
    public ResultDTO<FavoriteDTO> addFavorite(long product) {
        Comprador buyer = lockBuyer();
        Producto p = products.findById(product).orElseThrow(ApiException::missing);
        if ("INACTIVO".equals(p.getEstado())) throw ApiException.conflict("Producto inactivo");
        var old = favorites.findByIdcomprador_IdAndIdproducto_Id(buyer.getId(), product);
        Favorito favorite =
                old.orElseGet(
                        () -> {
                            Favorito f = new Favorito();
                            f.setIdcomprador(buyer);
                            f.setIdproducto(p);
                            f.setFecha(Times.now());
                            return favorites.save(f);
                        });
        return new ResultDTO<>(old.isEmpty(), favoriteView(favorite));
    }

    @Override
    public List<FavoriteDTO> favorites() {
        return favorites.findByIdcomprador_IdOrderByIdDesc(identity.buyer()).stream()
                .map(this::favoriteView)
                .toList();
    }

    @Override
    @Transactional
    public void removeFavorite(long product) {
        favorites.deleteByIdcomprador_IdAndIdproducto_Id(lockBuyer().getId(), product);
    }

    private CartDTO cartView(Carrito cart) {
        var items =
                details.findByIdcarrito_IdOrderByIdAsc(cart.getId()).stream()
                        .map(
                                d -> {
                                    var price = d.getPreciounitario();
                                    return new CartItemDTO(
                                            d.getId(),
                                            catalog.product(d.getIdproducto().getId()),
                                            d.getCantidad(),
                                            price,
                                            price.multiply(BigDecimal.valueOf(d.getCantidad())));
                                })
                        .toList();
        return new CartDTO(
                cart.getId(),
                items,
                items.stream().map(CartItemDTO::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Override
    @Transactional
    public CartDTO cart() {
        Carrito cart = active(lockBuyer());
        for (var d : details.findByIdcarrito_IdOrderByIdAsc(cart.getId())) {
            d.setPreciounitario(d.getIdproducto().getPreciounidad());
        }
        return cartView(cart);
    }

    @Override
    @Transactional
    public ResultDTO<CartDTO> add(AddItemRequest r) {
        Carrito cart = active(lockBuyer());
        Producto p = products.lockById(r.productId()).orElseThrow(ApiException::missing);
        var old = details.findByIdcarrito_IdAndIdproducto_Id(cart.getId(), p.getId());
        long quantity = (old.isPresent() ? old.get().getCantidad().longValue() : 0) + r.quantity();
        available(p, quantity);
        Carritodetalle detail = old.orElseGet(Carritodetalle::new);
        detail.setIdcarrito(cart);
        detail.setIdproducto(p);
        detail.setCantidad((int) quantity);
        detail.setPreciounitario(p.getPreciounidad());
        details.save(detail);
        return new ResultDTO<>(old.isEmpty(), cartView(cart));
    }

    private Carritodetalle ownItem(long item, long buyer) {
        return details.findByIdAndIdcarrito_Idcomprador_IdAndIdcarrito_Estado(item, buyer, "ACTIVO")
                .orElseThrow(ApiException::missing);
    }

    @Override
    @Transactional
    public CartDTO quantity(long item, int quantity) {
        var d = ownItem(item, lockBuyer().getId());
        var p = products.lockById(d.getIdproducto().getId()).orElseThrow(ApiException::missing);
        available(p, quantity);
        d.setCantidad(quantity);
        d.setPreciounitario(p.getPreciounidad());
        return cartView(d.getIdcarrito());
    }

    @Override
    @Transactional
    public void remove(long item) {
        details.delete(ownItem(item, lockBuyer().getId()));
    }

    private void available(Producto p, long qty) {
        if (qty < 1 || qty > Integer.MAX_VALUE) throw ApiException.bad("Cantidad inválida");
        if (!"ACTIVO".equals(p.getEstado()) || p.getStock() < qty) {
            throw ApiException.conflict("Producto " + p.getId() + ": disponibilidad insuficiente");
        }
    }

    private String fingerprint(long cart, List<Carritodetalle> rows) {
        StringBuilder value = new StringBuilder().append(cart);
        for (var d : rows)
            value.append('|')
                    .append(d.getIdproducto().getId())
                    .append(':')
                    .append(d.getCantidad())
                    .append(':')
                    .append(d.getPreciounitario().toPlainString());
        try {
            return HexFormat.of()
                    .formatHex(
                            MessageDigest.getInstance("SHA-256")
                                    .digest(value.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @Transactional
    public ResultDTO<PurchaseDTO> checkout(String key, Long expectedCart) {
        if (key == null || key.isBlank() || key.length() > 200) {
            throw ApiException.bad("Idempotency-Key obligatorio (máximo 200 caracteres)");
        }
        // Serializes all operations on one buyer's active cart and idempotency keys.
        Comprador buyer = lockBuyer();
        var previous = purchases.findByIdcomprador_IdAndClaveidempotencia(buyer.getId(), key);
        if (previous.isPresent()) {
            var v = previous.get();
            long oldCart = v.getCarrito().getId();
            if (expectedCart != null && expectedCart != oldCart)
                throw ApiException.conflict("La clave corresponde a otro carrito");
            if (expectedCart == null
                    && details.countByIdcarrito_Idcomprador_IdAndIdcarrito_Estado(
                                    buyer.getId(), "ACTIVO")
                            > 0) {
                throw ApiException.conflict(
                        "Usa una nueva clave para el nuevo carrito; para reintentar indica"
                                + " X-Cart-Id original");
            }
            var rows = details.findByIdcarrito_IdOrderByIdproducto_IdAsc(oldCart);
            if (!fingerprint(oldCart, rows).equals(v.getHuellaoperacion()))
                throw ApiException.conflict("El contenido de la operación cambió");
            return new ResultDTO<>(false, purchaseView(v));
        }
        Carrito cart = active(buyer);
        if (expectedCart != null && !expectedCart.equals(cart.getId()))
            throw ApiException.conflict("Carrito distinto del esperado");
        var rows = details.findByIdcarrito_IdOrderByIdproducto_IdAsc(cart.getId());
        if (rows.isEmpty()) throw ApiException.conflict("El carrito está vacío");
        BigDecimal total = BigDecimal.ZERO;
        List<Producto> locked = new ArrayList<>();
        // Ascending product IDs give every checkout the same lock order.
        for (var d : rows) {
            var p = products.lockById(d.getIdproducto().getId()).orElseThrow(ApiException::missing);
            available(p, d.getCantidad());
            if (p.getPreciounidad().compareTo(d.getPreciounitario()) != 0) {
                throw ApiException.conflict(
                        "Cambió el precio del producto "
                                + p.getId()
                                + ". Consulta nuevamente el carrito antes de confirmar");
            }
            total = total.add(d.getPreciounitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            locked.add(p);
        }
        Boletaventa v = new Boletaventa();
        v.setIdcomprador(buyer);
        v.setCarrito(cart);
        v.setClaveidempotencia(key);
        v.setHuellaoperacion(fingerprint(cart.getId(), rows));
        v.setPreciototal(total);
        v.setMetodopago("SIMULADO");
        v.setEstadoemision("PENDIENTE");
        v.setFechaventa(Times.now());
        purchases.save(v);
        for (int i = 0; i < rows.size(); i++) {
            var d = rows.get(i);
            var p = locked.get(i);
            int stock = p.getStock() - d.getCantidad();
            p.setStock(stock);
            p.setEstado(stock == 0 ? "AGOTADO" : "ACTIVO");
            Boletadetalle line = new Boletadetalle();
            line.setIdboletaventa(v);
            line.setIdproducto(p);
            line.setCantidad(d.getCantidad());
            line.setPreciounitario(d.getPreciounitario());
            line.setDescuento(BigDecimal.ZERO.setScale(2));
            purchaseDetails.save(line);
        }
        cart.setEstado("COMPRADO");
        return new ResultDTO<>(true, purchaseView(v));
    }

    private PurchaseDTO purchaseView(Boletaventa v) {
        var items =
                purchaseDetails.findByIdboletaventa_IdOrderByIdAsc(v.getId()).stream()
                        .map(
                                d ->
                                        new PurchaseItemDTO(
                                                d.getIdproducto().getId(),
                                                d.getIdproducto().getNombreproducto(),
                                                d.getCantidad(),
                                                d.getPreciounitario(),
                                                d.getDescuento(),
                                                d.getPreciounitario()
                                                        .multiply(
                                                                BigDecimal.valueOf(d.getCantidad()))
                                                        .subtract(d.getDescuento())))
                        .toList();
        return new PurchaseDTO(
                v.getId(),
                v.getFechaventa(),
                items,
                v.getPreciototal(),
                v.getMetodopago(),
                v.getEstadoemision());
    }

    @Override
    public PurchaseDTO purchase(long id) {
        return purchaseView(
                purchases
                        .findByIdAndIdcomprador_Id(id, identity.buyer())
                        .orElseThrow(ApiException::missing));
    }

    @Override
    public PageDTO<PurchaseDTO> purchases(int page, int size) {
        return Pages.view(
                purchases.findByIdcomprador_Id(
                        identity.buyer(),
                        Pages.request(
                                page, size, Sort.by(Sort.Direction.DESC, "fechaventa", "id"))),
                this::purchaseView);
    }
}
