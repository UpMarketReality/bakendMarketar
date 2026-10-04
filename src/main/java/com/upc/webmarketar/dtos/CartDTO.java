package com.upc.webmarketar.dtos;

import java.math.BigDecimal;
import java.util.List;

public record CartDTO(long cartId, List<CartItemDTO> items, BigDecimal total) {}

