package com.upc.webmarketar.security.service;

import com.upc.webmarketar.entities.Usuario;
import com.upc.webmarketar.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                                    "Usuario no encontrado: " + correo));

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPasswordhash())
                .roles(usuario.getIdrol().getNombre())
                .build();
    }
}
