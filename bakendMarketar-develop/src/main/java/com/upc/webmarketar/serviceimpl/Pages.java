package com.upc.webmarketar.serviceimpl;

import com.upc.webmarketar.dtos.PageDTO;
import com.upc.webmarketar.exceptions.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

final class Pages {
    private Pages() {}

    static Pageable request(int page, int size, Sort sort) {
        if (page < 0 || page > 100000 || size < 1 || size > 100) {
            throw ApiException.bad("page debe ser >=0 y size entre 1 y 100");
        }
        return PageRequest.of(page, size, sort);
    }

    static <E, D> PageDTO<D> view(Page<E> page, Function<E, D> mapper) {
        return new PageDTO<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
