package com.upc.webmarketar.serviceimpl;

import com.upc.webmarketar.dtos.ActualizarPerfilRequest;
import com.upc.webmarketar.dtos.CambiarPasswordRequest;
import com.upc.webmarketar.dtos.PerfilUsuarioResponse;
import com.upc.webmarketar.entities.Usuario;
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

    public UsuarioPerfilService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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
}
