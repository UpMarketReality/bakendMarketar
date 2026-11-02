package com.upc.webmarketar.security.services;

import com.upc.webmarketar.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository users;

    public CustomUserDetailsService(UsuarioRepository users) {
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        var user =
                users.findByCorreo(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario inexistente"));
        return User.withUsername(user.getCorreo())
                .password(user.getPasswordhash())
                .roles(user.getIdrol().getNombre())
                .build();
    }
}
