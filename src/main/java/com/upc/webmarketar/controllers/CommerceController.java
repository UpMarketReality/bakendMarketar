package com.upc.webmarketar.controllers;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.services.CommerceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CommerceController {
    private final CommerceService s;

    public CommerceController(CommerceService s) {
        this.s = s;
    }

    @PostMapping("/users/me/favorites/{productId}")
    public ResponseEntity<FavoriteDTO> favorite(@PathVariable long productId) {
        var r = s.addFavorite(productId);
        return ResponseEntity.status(r.created() ? 201 : 200).body(r.value());
    }

    @GetMapping("/users/me/favorites")
    public List<FavoriteDTO> favorites() {
        return s.favorites();
    }

    @DeleteMapping("/users/me/favorites/{productId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable long productId) {
        s.removeFavorite(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cart")
    public CartDTO cart() {
        return s.cart();
    }

    @PostMapping("/cart/items")
    public ResponseEntity<CartDTO> add(@Valid @RequestBody AddItemRequest r) {
        var x = s.add(r);
        return ResponseEntity.status(x.created() ? 201 : 200).body(x.value());
    }

    @PatchMapping("/cart/items/{itemId}")
    public CartDTO quantity(@PathVariable long itemId, @Valid @RequestBody QuantityRequest r) {
        return s.quantity(itemId, r.quantity());
    }

    @DeleteMapping("/cart/items/{itemId}")
    public ResponseEntity<Void> remove(@PathVariable long itemId) {
        s.remove(itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<PurchaseDTO> checkout(
            @RequestHeader("Idempotency-Key") String key,
            @RequestHeader(value = "X-Cart-Id", required = false) Long cart) {
        var r = s.checkout(key, cart);
        return ResponseEntity.status(r.created() ? 201 : 200).body(r.value());
    }

    @GetMapping("/purchases/me")
    public PageDTO<PurchaseDTO> purchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return s.purchases(page, size);
    }

    @GetMapping("/purchases/{purchaseId}")
    public PurchaseDTO purchase(@PathVariable long purchaseId) {
        return s.purchase(purchaseId);
    }
}
