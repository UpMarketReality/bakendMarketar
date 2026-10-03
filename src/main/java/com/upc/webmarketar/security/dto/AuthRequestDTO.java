package com.upc.webmarketar.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String correo;
    private String password;
}
