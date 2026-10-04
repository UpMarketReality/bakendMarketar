package com.upc.webmarketar.dtos;

public record ResultDTO<T>(boolean created, T value) {}