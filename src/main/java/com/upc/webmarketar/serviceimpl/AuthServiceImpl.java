package com.upc.webmarketar.serviceimpl;

import com.upc.webmarketar.entities.*;
import com.upc.webmarketar.exceptions.ApiException;
import com.upc.webmarketar.repositories.*;
import com.upc.webmarketar.security.dtos.*;
import com.upc.webmarketar.security.util.JwtTokens;
import com.upc.webmarketar.services.AuthService;

import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final UsuarioRepository users;
    private final RolRepository roles;
    private final CompradorRepository buyers;
    private final VendedorRepository sellers;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokens tokens;

    public AuthServiceImpl(
            UsuarioRepository users,
            RolRepository roles,
            CompradorRepository buyers,
            VendedorRepository sellers,
            PasswordEncoder encoder,
            AuthenticationManager authenticationManager,
            JwtTokens tokens) {
        this.users = users;
        this.roles = roles;
        this.buyers = buyers;
        this.sellers = sellers;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.tokens = tokens;
    }

    @Override
    @Transactional
    public UserDTO register(RegisterRequest r) {
        if (r.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw ApiException.bad("La contraseña excede 72 bytes UTF-8");
        }
        if ("VENDEDOR".equals(r.role())
                && (r.storeName() == null
                || r.storeName().isBlank()
                || r.ruc() == null
                || !r.ruc().matches("[0-9]{11}"))) {
            throw ApiException.bad("Vendedor requiere tienda y RUC de 11 dígitos");
        }
        Usuario user = new Usuario();
        user.setNombre(r.name().trim());
        user.setCorreo(r.email().trim().toLowerCase(Locale.ROOT));
        user.setPasswordhash(encoder.encode(r.password()));
        user.setIdrol(
                roles.findByNombre(r.role()).orElseThrow(() -> ApiException.bad("Rol inválido")));
        users.save(user);
        if ("COMPRADOR".equals(r.role())) {
            Comprador buyer = new Comprador();
            buyer.setUsuarioid(user);
            buyer.setDireccion(r.address());
            buyers.save(buyer);
        } else {
            Vendedor seller = new Vendedor();
            seller.setUsuarioid(user);
            seller.setNombretienda(r.storeName().trim());
            seller.setRuc(r.ruc());
            seller.setDescripciontienda(r.storeDescription());
            sellers.save(seller);
        }
        return userView(user);
    }

    @Override
    public LoginDTO login(LoginRequest input) {
        String email = input.correo().trim().toLowerCase(Locale.ROOT);
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(email, input.password()));
        } catch (AuthenticationException e) {
            throw new ApiException(401, "Correo o contraseña incorrectos");
        }
        Usuario user =
                users.findByCorreo(email)
                        .orElseThrow(
                                () -> new ApiException(401, "Correo o contraseña incorrectos"));
        var expires = tokens.expires();
        return new LoginDTO(
                tokens.issue(email, expires),
                Set.of("ROLE_" + user.getIdrol().getNombre()),
                expires,
                userView(user));
    }

    private UserDTO userView(Usuario user) {
        return new UserDTO(
                user.getId(), user.getNombre(), user.getCorreo(), user.getIdrol().getNombre());
    }
}
