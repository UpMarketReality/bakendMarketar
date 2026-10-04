package com.upc.webmarketar.dtos;

import java.util.List;

public record PageDTO<T>(
        List<T> content, int page, int size, long totalElements, long totalPages) {}
