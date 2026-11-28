package com.upc.webmarketar.security.dtos;

import java.time.OffsetDateTime;
import java.util.Set;

public record LoginDTO(String jwt, Set<String> roles, OffsetDateTime expiresAt, UserDTO user) {}
