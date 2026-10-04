package com.upc.webmarketar.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDTO {
    private String jwt;
    private Set<String> roles;
}
