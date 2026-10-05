package com.upc.webmarketar.controllers;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.security.dtos.*;
import com.upc.webmarketar.services.QuoteService;

import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class QuoteController {
    private final QuoteService s;

    public QuoteController(QuoteService s) {
        this.s = s;
    }

    @PostMapping("/quote-requests")
    public ResponseEntity<QuoteRequestDTO> create(@Valid @RequestBody CreateQuoteRequest r) {
        return ResponseEntity.status(201).body(s.create(r));
    }

    @GetMapping("/seller/quote-requests")
    public PageDTO<QuoteRequestDTO> open(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return s.requests(false, categoryId, page, size);
    }

    @GetMapping("/seller/quote-requests/{requestId}")
    public QuoteRequestDTO seller(@PathVariable long requestId) {
        return s.sellerRequest(requestId);
    }

    @PostMapping("/seller/quote-requests/{requestId}/offers")
    public ResponseEntity<OfferDTO> offer(
            @PathVariable long requestId, @Valid @RequestBody OfferRequest r) {
        return ResponseEntity.status(201).body(s.offer(requestId, r));
    }

    @GetMapping("/quote-requests/me")
    public PageDTO<QuoteRequestDTO> mine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return s.requests(true, null, page, size);
    }

    @GetMapping("/quote-requests/{requestId}")
    public QuoteRequestDTO own(@PathVariable long requestId) {
        return s.ownRequest(requestId);
    }

    @GetMapping("/quote-requests/{requestId}/offers")
    public List<OfferDTO> offers(@PathVariable long requestId) {
        return s.offers(requestId);
    }

    @PostMapping("/offers/{offerId}/accept")
    public ResponseEntity<OrderDTO> accept(@PathVariable long offerId) {
        var r = s.accept(offerId);
        return ResponseEntity.status(r.created() ? 201 : 200).body(r.value());
    }

    @PatchMapping("/quote-requests/{requestId}/cancel")
    public QuoteRequestDTO cancel(@PathVariable long requestId) {
        return s.cancel(requestId);
    }

    @GetMapping("/orders/me")
    public PageDTO<OrderDTO> orders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return s.orders(false, page, size);
    }

    @GetMapping("/orders/{orderId}")
    public OrderDTO order(@PathVariable long orderId) {
        return s.order(orderId, false);
    }

    @GetMapping("/seller/orders")
    public PageDTO<OrderDTO> sellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return s.orders(true, page, size);
    }

    @GetMapping("/seller/orders/{orderId}/specifications")
    public OrderDTO specifications(@PathVariable long orderId) {
        return s.order(orderId, true);
    }

    @PatchMapping("/seller/orders/{orderId}/status")
    public OrderDTO status(@PathVariable long orderId, @Valid @RequestBody StatusRequest r) {
        return s.status(orderId, r.status());
    }
}
