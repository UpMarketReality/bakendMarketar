package com.upc.webmarketar.serviceimpl;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.entities.Comprador;
import com.upc.webmarketar.entities.Usuario;
import com.upc.webmarketar.repositories.CompradorRepository;
import com.upc.webmarketar.repositories.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioPerfilService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final CompradorRepository compradorRepository;
    private final JsonMapper objectMapper;

    public UsuarioPerfilService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            CompradorRepository compradorRepository,
            JsonMapper objectMapper) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.compradorRepository = compradorRepository;
        this.objectMapper = objectMapper;
    }


    // Obtener el usuario desde la sesión JWT
    private Usuario usuarioActual(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "No autenticado"
            );
        }

        return repository
                .findByCorreo(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "No se encontró el usuario autenticado"
                ));
    }

    private PerfilUsuarioResponse convertirDTO(Usuario usuario) {
        return new PerfilUsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getDocumento()
        );
    }

    // GET /me
    @Transactional(readOnly = true)
    public PerfilUsuarioResponse obtenerMiPerfil(
            Authentication auth) {

        return convertirDTO(usuarioActual(auth));
    }

    // GET /{id}
    @Transactional(readOnly = true)
    public PerfilUsuarioResponse obtenerPorId(
            Long id,
            Authentication auth) {

        Usuario actual = usuarioActual(auth);

        boolean esAdministrador = auth.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMINISTRADOR")
                                || a.getAuthority().equals("ADMINISTRADOR")
                );

        if (!actual.getId().equals(id) && !esAdministrador) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para consultar este perfil"
            );
        }

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        return convertirDTO(usuario);
    }

    // PUT /me
    @Transactional
    public PerfilUsuarioResponse actualizarPerfil(
            ActualizarPerfilRequest request,
            Authentication auth) {

        Usuario usuario = usuarioActual(auth);

        String documento = request.documento().trim();

        if (repository.existsByDocumentoAndIdNot(
                documento, usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El documento ya está registrado"
            );
        }

        usuario.setNombre(request.nombre().trim());
        usuario.setTelefono(request.telefono().trim());
        usuario.setDocumento(documento);

        Usuario actualizado = repository.save(usuario);

        return convertirDTO(actualizado);
    }

    // PATCH /me/password
    @Transactional
    public void cambiarPassword(
            CambiarPasswordRequest request,
            Authentication auth) {

        Usuario usuario = usuarioActual(auth);

        if (!passwordEncoder.matches(
                request.passwordActual(),
                usuario.getPasswordhash())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña actual es incorrecta"
            );
        }

        if (passwordEncoder.matches(
                request.passwordNueva(),
                usuario.getPasswordhash())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nueva contraseña debe ser diferente"
            );
        }

        usuario.setPasswordhash(
                passwordEncoder.encode(request.passwordNueva())
        );

        repository.save(usuario);
    }
    // Obtener el comprador asociado al JWT
    private Comprador obtenerCompradorActual(
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "No autenticado"
            );
        }

        String correo = auth.getName();

        return compradorRepository
                .buscarPorCorreoUsuario(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un perfil de comprador para esta cuenta"
                ));
    }

    // GET: Obtener dirección
    @Transactional(readOnly = true)
    public DireccionCompradorResponse obtenerMiDireccion(
            Authentication auth) {

        Comprador comprador = obtenerCompradorActual(auth);

        return new DireccionCompradorResponse(
                comprador.getDireccion()
        );
    }

    // PUT: Guardar o actualizar dirección
    @Transactional
    public DireccionCompradorResponse actualizarMiDireccion(
            DireccionCompradorRequest request,
            Authentication auth) {

        Comprador comprador = obtenerCompradorActual(auth);

        // Normalizar los valores del formulario
        DireccionCompradorRequest datos =
                new DireccionCompradorRequest(
                        request.calle().trim(),
                        request.region().trim(),
                        request.distrito().trim(),
                        request.referencia() == null
                                ? ""
                                : request.referencia().trim()
                );

        String direccion;

        try {
            direccion = objectMapper.writeValueAsString(datos);
        } catch (JacksonException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo procesar la dirección",
                    e
            );
        }

        // Evitar superar el límite de la columna
        if (direccion.length() > 300) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La dirección completa supera los 300 caracteres"
            );
        }

        // Guardar en la única columna direccion
        comprador.setDireccion(direccion);

        compradorRepository.save(comprador);

        return new DireccionCompradorResponse(
                comprador.getDireccion()
        );
    }
}


