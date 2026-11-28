package com.upc.webmarketar.serviceimpl;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.entities.Imagenproducto;
import com.upc.webmarketar.entities.Producto;
import com.upc.webmarketar.entities.Vendedor;
import com.upc.webmarketar.exceptions.ApiException;
import com.upc.webmarketar.infrastructure.MediaStorage;
import com.upc.webmarketar.repositories.*;
import com.upc.webmarketar.security.services.Identity;
import com.upc.webmarketar.services.CatalogService;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {
    private final ProductoRepository products;
    private final ImagenProductoRepository images;
    private final CategoriaProductoRepository categories;
    private final CategoriaPrototipoRepository prototypeCategories;
    private final VendedorRepository sellers;
    private final Identity identity;
    private final MediaStorage media;

    public CatalogServiceImpl(
            ProductoRepository products,
            ImagenProductoRepository images,
            CategoriaProductoRepository categories,
            CategoriaPrototipoRepository prototypeCategories,
            VendedorRepository sellers,
            Identity identity,
            MediaStorage media) {
        this.products = products;
        this.images = images;
        this.categories = categories;
        this.prototypeCategories = prototypeCategories;
        this.sellers = sellers;
        this.identity = identity;
        this.media = media;
    }

    private ProductDTO view(Producto p) {
        var imgs =
                images.findByIdproducto_IdOrderByEsprincipalDescIdAsc(p.getId()).stream()
                        .map(i -> new ImageDTO(i.getId(), i.getUrl(), i.getEsprincipal()))
                        .toList();
        return new ProductDTO(
                p.getId(),
                p.getNombreproducto(),
                p.getDescripcion(),
                p.getPreciounidad(),
                p.getPrecioxmayor(),
                p.getStock(),
                p.getEstado(),
                p.getDimensiones(),
                p.getMaterial(),
                p.getColor(),
                p.getCategoria().getId(),
                p.getVendedor().getId(),
                p.getVendedor().getNombretienda(),
                imgs.isEmpty() ? null : imgs.getFirst().url(),
                imgs);
    }

    @Override
    public ProductDTO product(long id) {
        return view(products.findById(id).orElseThrow(ApiException::missing));
    }

    @Override
    public ProductDTO publicProduct(long id) {
        Producto p = products.findById(id).orElseThrow(ApiException::missing);
        if (!Set.of("ACTIVO", "AGOTADO").contains(p.getEstado())) throw ApiException.missing();
        return view(p);
    }

    private Producto owned(long id, boolean lock) {
        Producto p =
                (lock ? products.lockById(id) : products.findById(id))
                        .orElseThrow(ApiException::missing);
        if (p.getVendedor().getId() != identity.seller()) throw ApiException.missing();
        return p;
    }

    @Override
    @PreAuthorize("hasRole('VENDEDOR')")
    public ProductDTO own(long id) {
        return view(owned(id, false));
    }

    @Override
    public PageDTO<ProductDTO> list(
            String search,
            Long category,
            BigDecimal min,
            BigDecimal max,
            int page,
            int size,
            boolean own) {
        var pageable = Pages.request(page, size, Sort.by("id"));
        if ((min != null && min.signum() < 0)
                || (max != null && max.signum() < 0)
                || (min != null && max != null && min.compareTo(max) > 0)) {
            throw ApiException.bad("Rango de precios inválido");
        }
        String pattern =
                search == null || search.isBlank()
                        ? null
                        : "%"
                                + search.toLowerCase(Locale.ROOT)
                                        .replace("!", "!!")
                                        .replace("%", "!%")
                                        .replace("_", "!_")
                                + "%";
        return Pages.view(
                products.search(
                        own ? identity.seller() : null, pattern, category, min, max, pageable),
                this::view);
    }

    private void apply(Producto p, ProductRequest r) {
        p.setNombreproducto(r.name());
        p.setDescripcion(r.description());
        p.setPreciounidad(r.unitPrice().setScale(2));
        p.setPrecioxmayor(r.wholesalePrice() == null ? null : r.wholesalePrice().setScale(2));
        p.setDimensiones(r.dimensions());
        p.setMaterial(r.material());
        p.setColor(r.color());
        p.setStock(r.stock());
        p.setCategoria(
                categories
                        .findById(r.categoryId())
                        .orElseThrow(() -> ApiException.bad("Categoría inválida")));
        if (!"INACTIVO".equals(p.getEstado())) p.setEstado(r.stock() == 0 ? "AGOTADO" : "ACTIVO");
    }

    private void addImage(Producto p, MultipartFile file, boolean principal) {
        Imagenproducto image = new Imagenproducto();
        image.setIdproducto(p);
        image.setUrl(media.product(file));
        image.setEsprincipal(principal);
        images.save(image);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public ProductDTO create(ProductRequest r, List<MultipartFile> files) {
        checkImages(files);
        if (files == null || files.isEmpty()) throw ApiException.bad("Incluye al menos una imagen");
        Producto p = new Producto();
        apply(p, r);
        p.setFechapublicacion(Times.now());
        p.setVendedor(sellers.getReferenceById(identity.seller()));
        products.save(p);
        for (int i = 0; i < files.size(); i++) addImage(p, files.get(i), i == 0);
        return view(p);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public ProductDTO update(long id, ProductRequest r, List<MultipartFile> files) {
        Producto p = owned(id, true);
        checkImages(files);
        var old = images.findByIdproducto_IdOrderByIdAsc(id);
        Set<Long> existing = new HashSet<>(old.stream().map(Imagenproducto::getId).toList());
        Set<Long> keep =
                r.retainedImageIds() == null ? existing : new HashSet<>(r.retainedImageIds());
        if (!existing.containsAll(keep)) throw ApiException.bad("Imagen ajena o inexistente");
        int count = keep.size() + (files == null ? 0 : files.size());
        if (count < 1 || count > 10)
            throw ApiException.bad("El producto requiere entre 1 y 10 imágenes");
        apply(p, r);
        for (var image : old) {
            if (!keep.contains(image.getId())) {
                images.delete(image);
                media.deleteProductAfterCommit(image.getUrl());
            } else {
                image.setEsprincipal(false);
            }
        }
        // Release the partial unique principal index before choosing the new main image.
        images.flush();
        if (files != null) for (var file : files) addImage(p, file, false);
        images.flush();
        var retained = images.findByIdproducto_IdOrderByIdAsc(id);
        retained.getFirst().setEsprincipal(true);
        images.flush();
        return view(p);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public ProductDTO status(long id, boolean active) {
        Producto p = owned(id, true);
        p.setEstado(active ? (p.getStock() > 0 ? "ACTIVO" : "AGOTADO") : "INACTIVO");
        return view(p);
    }

    private void checkImages(List<MultipartFile> files) {
        if (files != null && files.size() > 10) throw ApiException.bad("Máximo 10 imágenes");
    }

    @Override
    public List<CategoryDTO> categories(boolean prototypes) {
        if (prototypes)
            return prototypeCategories.findAll(Sort.by("id")).stream()
                    .map(
                            c ->
                                    new CategoryDTO(
                                            c.getId(),
                                            c.getNombre(),
                                            c.getDescripcion(),
                                            c.getTipo()))
                    .toList();
        return categories.findAll(Sort.by("id")).stream()
                .map(
                        c ->
                                new CategoryDTO(
                                        c.getId(), c.getNombre(), c.getDescripcion(), c.getTipo()))
                .toList();
    }

    private StoreDTO storeView(Vendedor seller) {
        return new StoreDTO(
                seller.getId(),
                seller.getNombretienda(),
                seller.getDescripciontienda(),
                seller.getRuc());
    }

    @Override
    @PreAuthorize("hasRole('VENDEDOR')")
    public StoreDTO store() {
        return storeView(sellers.findById(identity.seller()).orElseThrow(ApiException::missing));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public StoreDTO store(StoreRequest r) {
        var seller = sellers.findById(identity.seller()).orElseThrow(ApiException::missing);
        seller.setNombretienda(r.storeName());
        seller.setDescripciontienda(r.storeDescription());
        return storeView(seller);
    }
}
